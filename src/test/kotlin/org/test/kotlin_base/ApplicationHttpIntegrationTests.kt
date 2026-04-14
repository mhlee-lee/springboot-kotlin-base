package org.test.kotlin_base

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.test.kotlin_base.common.constant.CommonConstant.API_VERSION_V1
import org.test.kotlin_base.common.errors.CommonErrorCode
import org.test.kotlin_base.common.errors.ErrorFieldNames
import org.test.kotlin_base.common.errors.ErrorSource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ApplicationHttpIntegrationTests {
    @LocalServerPort
    private var port: Int = 0

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://127.0.0.1:$port")
            .build()
    }

    @Test
    fun `hello endpoint returns greeting`() {
        webTestClient.get()
            .uri("/hello/$API_VERSION_V1/hello")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo("Hello")
    }

    @Test
    fun `sample read endpoints respond successfully`() {
        webTestClient.get()
            .uri("/sample/$API_VERSION_V1/sample")
            .exchange()
            .expectStatus().isOk
            .expectBody().isEmpty

        webTestClient.get()
            .uri("/sample/$API_VERSION_V1/addressScope")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$[0].id").isEqualTo("scope-1")
            .jsonPath("$[0].addressType").isEqualTo("RESIDENTIAL")
            .jsonPath("$[1].id").isEqualTo("scope-2")
            .jsonPath("$[1].addressType").isEqualTo("COMMERCIAL")
    }

    @Test
    fun `sample put endpoint binds path query header and body`() {
        webTestClient.put()
            .uri("/sample/$API_VERSION_V1/sample/FEMALE?name=tester")
            .header("age", "29")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"id":"sample-1","ttl":60}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("tester")
            .jsonPath("$.age").isEqualTo(29)
            .jsonPath("$.gender").isEqualTo("FEMALE")
            .jsonPath("$.id").isEqualTo("sample-1")
            .jsonPath("$.ttl").isEqualTo(60)
    }

    @Test
    fun `sample put endpoint returns bad request when age header is missing`() {
        webTestClient.put()
            .uri("/sample/$API_VERSION_V1/sample/FEMALE?name=tester")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"id":"sample-1","ttl":60}""")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("X-Trace-Id")
            .expectBody()
            .jsonPath("$.code").isEqualTo(CommonErrorCode.INVALID_HEADER_PARAMETER.code)
            .jsonPath("$.path").isEqualTo("/sample/1.0/sample/FEMALE")
            .jsonPath("$.traceId").exists()
            .jsonPath("$.errors[0].source").isEqualTo(ErrorSource.HEADER.wireName)
            .jsonPath("$.errors[0].field").isEqualTo("age")
    }

    @Test
    fun `sample put endpoint returns bad request when age header is invalid`() {
        webTestClient.put()
            .uri("/sample/$API_VERSION_V1/sample/FEMALE?name=tester")
            .header("age", "xx")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"id":"sample-1","ttl":60}""")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.code").isEqualTo(CommonErrorCode.INVALID_HEADER_PARAMETER.code)
            .jsonPath("$.traceId").exists()
            .jsonPath("$.errors[0].source").isEqualTo(ErrorSource.HEADER.wireName)
            .jsonPath("$.errors[0].field").isEqualTo("age")
    }

    @Test
    fun `sample put endpoint returns bad request when body is empty`() {
        webTestClient.put()
            .uri("/sample/$API_VERSION_V1/sample/FEMALE?name=tester")
            .header("age", "29")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.code").isEqualTo(CommonErrorCode.EMPTY_BODY.code)
            .jsonPath("$.traceId").exists()
            .jsonPath("$.errors[0].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[0].field").isEqualTo(ErrorFieldNames.BODY)
    }

    @Test
    fun `sample put endpoint returns bad request when gender is invalid`() {
        webTestClient.put()
            .uri("/sample/$API_VERSION_V1/sample/UNKNOWN?name=tester")
            .header("age", "29")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"id":"sample-1","ttl":60}""")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.code").isEqualTo(CommonErrorCode.INVALID_PARAMETER.code)
            .jsonPath("$.traceId").exists()
            .jsonPath("$.errors[0].source").isEqualTo(ErrorSource.PATH.wireName)
            .jsonPath("$.errors[0].field").isEqualTo("gender")
    }

    @Test
    fun `sample put endpoint returns validation field errors when body is invalid`() {
        webTestClient.put()
            .uri("/sample/$API_VERSION_V1/sample/FEMALE?name=tester")
            .header("age", "29")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"id":"","ttl":0}""")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_FAIL.code)
            .jsonPath("$.traceId").exists()
            .jsonPath("$.errors[0].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[0].field").isEqualTo("id")
            .jsonPath("$.errors[1].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[1].field").isEqualTo("ttl")
    }

    @Test
    fun `unsupported api version returns bad request`() {
        webTestClient.get()
            .uri("/hello/9.9/hello")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `unknown path returns not found code`() {
        webTestClient.get()
            .uri("/unknown")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().exists("X-Trace-Id")
            .expectBody()
            .jsonPath("$.code").isEqualTo(CommonErrorCode.NOT_FOUND.code)
            .jsonPath("$.path").isEqualTo("/unknown")
            .jsonPath("$.traceId").exists()
    }

    @Test
    fun `provided trace id is reused in response header and body`() {
        val traceId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

        webTestClient.get()
            .uri("/unknown")
            .header("X-Trace-Id", traceId)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().valueEquals("X-Trace-Id", traceId)
            .expectBody()
            .jsonPath("$.traceId").isEqualTo(traceId)
    }

    @Test
    fun `traceparent trace id is reused when custom header is absent`() {
        val traceId = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
        val traceParent = "00-$traceId-cccccccccccccccc-01"

        webTestClient.get()
            .uri("/unknown")
            .header("traceparent", traceParent)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().valueEquals("X-Trace-Id", traceId)
            .expectBody()
            .jsonPath("$.traceId").isEqualTo(traceId)
    }
}
