package com.example.skeleton.common.config.accesslog

import com.example.skeleton.common.config.TraceIdWebFilter
import net.logstash.logback.marker.Markers
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.webflux.autoconfigure.WebHttpHandlerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.PathContainer
import org.springframework.http.server.reactive.*
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AccessLogProperties::class)
class AccessLogConfiguration {
    @Bean
    fun loggingCustomizer(objectMapper: JsonMapper, properties: AccessLogProperties): WebHttpHandlerBuilderCustomizer {
        val masker = AccessLogMasker(objectMapper, properties.maskedKeys)
        val adminPatterns = properties.adminPathPatterns.map(PathPatternParser.defaultInstance::parse)
        val systemPatterns = properties.systemPathPatterns.map(PathPatternParser.defaultInstance::parse)

        return WebHttpHandlerBuilderCustomizer { builder ->
            builder.httpHandlerDecorator { delegate ->
                HttpHandler handler@{ request, response ->
                    if (!ACCESS_LOGGER.isInfoEnabled) {
                        return@handler delegate.handle(request, response)
                    }

                    val startedAt = Instant.now()
                    val requestCapture = BoundedBodyCapture(
                        limit = properties.maxBodySize,
                        captureBody = request.headers.isAccessLogBodyCapturable(properties.maxBodySize),
                    )
                    val responseCapture = BoundedBodyCapture(properties.maxBodySize, captureBody = true)

                    val requestDecorator = object : ServerHttpRequestDecorator(request) {
                        override fun getBody(): Flux<DataBuffer> = super.getBody().doOnNext(requestCapture::observe)
                    }
                    val responseDecorator = object : ServerHttpResponseDecorator(response) {
                        override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> =
                            super.writeWith(Flux.from(body).doOnNext(responseCapture::observe))

                        override fun writeAndFlushWith(body: Publisher<out Publisher<out DataBuffer>>): Mono<Void> =
                            super.writeAndFlushWith(
                                Flux.from(body).map { publisher ->
                                    Flux.from(publisher).doOnNext(responseCapture::observe)
                                },
                            )
                    }

                    delegate.handle(requestDecorator, responseDecorator).doFinally {
                        try {
                            val endedAt = Instant.now()
                            writeLog(
                                requestRecord(
                                    request = request,
                                    response = response,
                                    capture = requestCapture,
                                    declaredSize = request.headers.contentLength,
                                    startedAt = startedAt,
                                    masker = masker,
                                    adminPatterns = adminPatterns,
                                    systemPatterns = systemPatterns,
                                ),
                            )
                            writeLog(
                                responseRecord(
                                    response = response,
                                    capture = responseCapture,
                                    durationMs = Duration.between(startedAt, endedAt)
                                        .toMillis()
                                        .coerceAtLeast(0L),
                                    timestamp = endedAt,
                                    objectMapper = objectMapper,
                                ),
                            )
                        } catch (_: Exception) {
                            // Access logging must not change or add noise to the completed HTTP exchange.
                        }
                    }
                }
            }
        }
    }

    private fun requestRecord(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        capture: BoundedBodyCapture,
        declaredSize: Long,
        startedAt: Instant,
        masker: AccessLogMasker,
        adminPatterns: List<PathPattern>,
        systemPatterns: List<PathPattern>,
    ): RequestAccessLog = RequestAccessLog(
        ts = formatTimestamp(startedAt),
        host = request.headers.getFirst(HttpHeaders.HOST)
            ?: request.localAddress?.let { "${it.hostString}:${it.port}" }.orEmpty(),
        metd = request.method.name(),
        type = classifyPath(request.uri.rawPath, adminPatterns, systemPatterns),
        ct = request.headers.contentType?.toString().orEmpty(),
        uri = request.uri.rawPath,
        qs = request.uri.rawQuery.orEmpty(),
        body = requestBody(request, capture, masker),
        reqSize = declaredSize.takeIf { it >= 0 } ?: capture.totalBytes,
        cli = AccessLogClient(
            ip = clientIp(request),
            agent = request.headers.getFirst(HttpHeaders.USER_AGENT).orEmpty(),
        ),
        usr = AccessLogUser(
            id = request.headers.getFirst(REQUESTER_ID_HEADER).orEmpty(),
            org = request.headers.getFirst(ORG_ID_HEADER).orEmpty(),
            prj = request.headers.getFirst(PROJECT_ID_HEADER).orEmpty(),
        ),
        tid = response.headers.getFirst(TraceIdWebFilter.TRACE_ID_HEADER).orEmpty(),
    )

    private fun responseRecord(
        response: ServerHttpResponse,
        capture: BoundedBodyCapture,
        durationMs: Long,
        timestamp: Instant,
        objectMapper: JsonMapper,
    ): ResponseAccessLog {
        val status = response.statusCode ?: HttpStatus.OK
        return ResponseAccessLog(
            ts = formatTimestamp(timestamp),
            code = status.value(),
            resSize = capture.totalBytes,
            dur = durationMs,
            err = responseError(response, capture, status.value(), objectMapper),
            tid = response.headers.getFirst(TraceIdWebFilter.TRACE_ID_HEADER).orEmpty(),
        )
    }

    private fun requestBody(request: ServerHttpRequest, capture: BoundedBodyCapture, masker: AccessLogMasker): Any {
        val bytes = capture.bytesOrNull()?.takeIf(ByteArray::isNotEmpty) ?: return ""
        return if (request.headers.contentType.isAccessLogJson()) {
            masker.maskJson(bytes, StandardCharsets.UTF_8)
        } else {
            String(bytes, StandardCharsets.UTF_8)
        }
    }

    private fun responseError(
        response: ServerHttpResponse,
        capture: BoundedBodyCapture,
        status: Int,
        objectMapper: JsonMapper,
    ): AccessLogError {
        if (status < HttpStatus.BAD_REQUEST.value() || !response.headers.contentType.isAccessLogJson()) {
            return AccessLogError()
        }
        val bytes = capture.bytesOrNull()?.takeIf(ByteArray::isNotEmpty) ?: return AccessLogError()
        val root = runCatching { objectMapper.readTree(bytes) }.getOrNull() ?: return AccessLogError()
        val nested = root.path("error")
        return AccessLogError(
            code = text(root, "code").ifEmpty { text(nested, "code") },
            msg = text(root, "message").ifEmpty { text(nested, "message") },
        )
    }

    private fun classifyPath(
        rawPath: String,
        adminPatterns: List<PathPattern>,
        systemPatterns: List<PathPattern>,
    ): String {
        val path = PathContainer.parsePath(rawPath)
        return when {
            systemPatterns.any { it.matches(path) } -> SYSTEM_TYPE
            adminPatterns.any { it.matches(path) } -> ADMIN_TYPE
            else -> USER_TYPE
        }
    }

    private fun clientIp(request: ServerHttpRequest): String = request.headers
        .getFirst(FORWARDED_FOR_HEADER)
        ?.split(',')
        ?.firstOrNull { it.isNotBlank() }
        ?.trim()
        ?: request.headers.getFirst(REAL_IP_HEADER)?.takeIf { it.isNotBlank() }
        ?: request.remoteAddress?.address?.hostAddress.orEmpty()

    private fun writeLog(record: Any) {
        runCatching {
            ACCESS_LOGGER.info(Markers.appendFields(record), "")
        }
    }

    private fun text(node: JsonNode, field: String): String = node.path(field).asString("")

    private fun formatTimestamp(instant: Instant): String = TIMESTAMP_FORMATTER.format(instant)

    companion object {
        private const val REQUESTER_ID_HEADER = "X-Requester-Id"
        private const val ORG_ID_HEADER = "X-Org-Id"
        private const val PROJECT_ID_HEADER = "X-Project-Id"
        private const val FORWARDED_FOR_HEADER = "X-Forwarded-For"
        private const val REAL_IP_HEADER = "X-Real-IP"
        private const val USER_TYPE = "user"
        private const val ADMIN_TYPE = "admin"
        private const val SYSTEM_TYPE = "system"
        private const val ACCESS_LOGGER_NAME = "api.raw"
        private val ACCESS_LOGGER = LoggerFactory.getLogger(ACCESS_LOGGER_NAME)
        private val TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
            .appendInstant(3)
            .toFormatter()
    }
}

internal class BoundedBodyCapture(
    private val limit: Int,
    private val captureBody: Boolean,
) {
    private val output = ByteArrayOutputStream(minOf(limit, 1024))
    private var overflowed = false
    private var failed = false

    var totalBytes: Long = 0
        private set

    fun observe(buffer: DataBuffer) {
        try {
            val length = buffer.readableByteCount()
            totalBytes += length

            if (!captureBody || overflowed || failed) {
                return
            }

            if (totalBytes > limit) {
                overflowed = true
                output.reset()
                return
            }

            val bytes = ByteArray(length)
            buffer.toByteBuffer(ByteBuffer.wrap(bytes))
            output.write(bytes)
        } catch (_: Exception) {
            failed = true
            output.reset()
        }
    }

    fun bytesOrNull(): ByteArray? = if (captureBody && !overflowed && !failed) {
        output.toByteArray()
    } else {
        null
    }
}

private val ACCESS_LOG_JSON_MEDIA_TYPES = listOf(
    MediaType.APPLICATION_JSON,
    MediaType("application", "*+json"),
)

private val ACCESS_LOG_TEXTUAL_MEDIA_TYPES = ACCESS_LOG_JSON_MEDIA_TYPES + listOf(
    MediaType.APPLICATION_XML,
    MediaType.APPLICATION_FORM_URLENCODED,
    MediaType.TEXT_PLAIN,
    MediaType("application", "*+xml"),
)

internal fun HttpHeaders.isAccessLogBodyCapturable(maxBodySize: Int): Boolean =
    contentType.isAccessLogTextual() && contentLength <= maxBodySize

internal fun MediaType?.isAccessLogTextual(): Boolean {
    val mediaType = this ?: return false
    return ACCESS_LOG_TEXTUAL_MEDIA_TYPES.any { it.includes(mediaType) }
}

internal fun MediaType?.isAccessLogJson(): Boolean {
    val mediaType = this ?: return false
    return ACCESS_LOG_JSON_MEDIA_TYPES.any { it.includes(mediaType) }
}
