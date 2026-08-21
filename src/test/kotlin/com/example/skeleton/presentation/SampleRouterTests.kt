package com.example.skeleton.presentation

import com.epages.restdocs.apispec.ConstrainedFields
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.document
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.resourceDetails
import com.example.skeleton.application.sample.SampleService
import com.example.skeleton.application.sample.model.CreateSampleCommand
import com.example.skeleton.application.sample.model.SampleSearchQuery
import com.example.skeleton.application.sample.model.UpdateSampleCommand
import com.example.skeleton.common.config.TraceIdWebFilter
import com.example.skeleton.common.config.TraceLoggingConfiguration
import com.example.skeleton.common.constant.CommonConstant.API_VERSION_V1
import com.example.skeleton.common.extensions.withDisplayEnum
import com.example.skeleton.common.utils.MessageConverter
import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.domain.sample.model.SampleStatus
import com.example.skeleton.presentaion.SampleHandler
import com.example.skeleton.presentaion.SampleRouter
import com.example.skeleton.presentaion.protocol.CreateSampleRequest
import com.example.skeleton.presentaion.protocol.UpdateSampleRequest
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.snippet.Snippet
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.ApiVersionConfigurer
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import java.util.function.Consumer
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@ContextConfiguration(classes = [SampleRouterTests.TestConfiguration::class])
class SampleRouterTests {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var sampleService: SampleService

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        clearMocks(sampleService)

        val restDocsConfigurer = documentationConfiguration(restDocumentation)
        restDocsConfigurer.operationPreprocessors()
            .withRequestDefaults(
                modifyUris().scheme("http").host("localhost").port(DOCUMENTATION_PORT),
                prettyPrint(),
            )
            .withResponseDefaults(prettyPrint())

        webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
            .configureClient()
            .filter(restDocsConfigurer)
            .build()
    }

    @Test
    fun `documents searching samples`() {
        val result = Sample(id = 1, name = "Alice", age = 30, status = SampleStatus.ACTIVE)
        coEvery { sampleService.searchSamples(any()) } returns listOf(result)

        webTestClient.get()
            .uri(
                "/sample/{version}/samples?name=Ali&minAge=20&maxAge=40&status=ACTIVE",
                API_VERSION_V1,
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].name").isEqualTo("Alice")
            .consumeWith(
                documentation(
                    identifier = "samples-search",
                    summary = "Sample 목록 검색",
                    description = "이름, 나이 범위, 상태 조건으로 Sample 목록을 검색합니다.",
                    pathParameters(versionPathParameter()),
                    queryParameters(
                        parameterWithName("name").description("이름에 포함될 문자열").optional(),
                        parameterWithName("minAge").description("최소 나이, 0 이상").optional(),
                        parameterWithName("maxAge").description("최대 나이, 200 이하").optional(),
                        parameterWithName("status")
                            .withDisplayEnum<SampleStatus>("상태")
                            .optional(),
                    ),
                    responseFields(*sampleResponseFields("[].")),
                ),
            )

        coVerify(exactly = 1) {
            sampleService.searchSamples(
                SampleSearchQuery(
                    name = "Ali",
                    minAge = 20,
                    maxAge = 40,
                    status = SampleStatus.ACTIVE,
                ),
            )
        }
    }

    @Test
    fun `documents searching samples by status`() {
        val result = Sample(id = 1, name = "Alice", age = 30, status = SampleStatus.ACTIVE)
        coEvery { sampleService.searchSamplesByStatus(SampleStatus.ACTIVE) } returns listOf(result)

        webTestClient.get()
            .uri("/sample/{version}/samples/status/{status}", API_VERSION_V1, "ACTIVE")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].status").isEqualTo("ACTIVE")
            .consumeWith(
                documentation(
                    identifier = "samples-search-by-status",
                    summary = "상태별 Sample 목록 조회",
                    description = "지정한 상태에 해당하는 Sample 목록을 조회합니다.",
                    pathParameters(
                        versionPathParameter(),
                        parameterWithName("status").withDisplayEnum<SampleStatus>("상태"),
                    ),
                    responseFields(*sampleResponseFields("[].")),
                ),
            )

        coVerify(exactly = 1) { sampleService.searchSamplesByStatus(SampleStatus.ACTIVE) }
    }

    @Test
    fun `documents getting a sample`() {
        val traceId = "router-mdc-trace-id"
        val result = Sample(id = 1, name = "Bob", age = 25, status = SampleStatus.ACTIVE)
        coEvery { sampleService.getSample(1) } coAnswers {
            assertEquals(
                traceId,
                MDC.get(TraceIdWebFilter.MDC_TRACE_ID_KEY),
                "Service 진입 시점의 MDC traceId",
            )
            withContext(Dispatchers.IO) {
                assertEquals(
                    traceId,
                    MDC.get(TraceIdWebFilter.MDC_TRACE_ID_KEY),
                    "Dispatcher 전환 후 MDC traceId",
                )
            }
            result
        }

        webTestClient.get()
            .uri("/sample/{version}/samples/{id}", API_VERSION_V1, 1)
            .header(TraceIdWebFilter.TRACE_ID_HEADER, traceId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.name").isEqualTo("Bob")
            .consumeWith(
                documentation(
                    identifier = "samples-get",
                    summary = "Sample 단건 조회",
                    description = "식별자로 Sample 한 건을 조회합니다.",
                    pathParameters(
                        versionPathParameter(),
                        parameterWithName("id").description("Sample 식별자"),
                    ),
                    responseFields(*sampleResponseFields()),
                ),
            )

        coVerify(exactly = 1) { sampleService.getSample(1) }
    }

    @Test
    fun `documents creating a sample`() {
        val command = CreateSampleCommand(name = "Alice", age = 30, status = SampleStatus.ACTIVE)
        val result = Sample(id = 1, name = "Alice", age = 30, status = SampleStatus.ACTIVE)
        coEvery { sampleService.createSample(command) } returns result

        webTestClient.post()
            .uri("/sample/{version}/samples", API_VERSION_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(CreateSampleRequest(name = "Alice", age = 30, status = SampleStatus.ACTIVE))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.name").isEqualTo("Alice")
            .consumeWith(
                documentation(
                    identifier = "samples-create",
                    summary = "Sample 생성",
                    description = "새 Sample을 생성합니다.",
                    pathParameters(versionPathParameter()),
                    requestFields(*sampleRequestFields(CreateSampleRequest::class.java)),
                    responseFields(*sampleResponseFields()),
                ),
            )

        coVerify(exactly = 1) { sampleService.createSample(command) }
    }

    @Test
    fun `documents updating a sample`() {
        val command = UpdateSampleCommand(
            id = 1,
            name = "After",
            age = 31,
            status = SampleStatus.INACTIVE,
            modifiedBy = "document-writer",
        )
        val result = Sample(id = 1, name = "After", age = 31, status = SampleStatus.INACTIVE)
        coEvery { sampleService.updateSample(command) } returns result

        webTestClient.put()
            .uri("/sample/{version}/samples/{id}", API_VERSION_V1, 1)
            .header(MODIFIED_BY_HEADER, "document-writer")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(UpdateSampleRequest(name = "After", age = 31, status = SampleStatus.INACTIVE))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.name").isEqualTo("After")
            .jsonPath("$.status").isEqualTo("INACTIVE")
            .consumeWith(
                documentation(
                    identifier = "samples-update",
                    summary = "Sample 수정",
                    description = "식별자에 해당하는 Sample을 수정합니다.",
                    pathParameters(
                        versionPathParameter(),
                        parameterWithName("id").description("Sample 식별자"),
                    ),
                    requestHeaders(
                        headerWithName(MODIFIED_BY_HEADER).description("수정 요청자 식별값"),
                    ),
                    requestFields(*sampleRequestFields(UpdateSampleRequest::class.java)),
                    responseFields(*sampleResponseFields()),
                ),
            )

        coVerify(exactly = 1) { sampleService.updateSample(command) }
    }

    @Test
    fun `documents deleting a sample`() {
        coEvery { sampleService.deleteSample(1) } returns Unit

        webTestClient.delete()
            .uri("/sample/{version}/samples/{id}", API_VERSION_V1, 1)
            .exchange()
            .expectStatus().isNoContent
            .expectBody()
            .consumeWith(
                documentation(
                    identifier = "samples-delete",
                    summary = "Sample 삭제",
                    description = "식별자에 해당하는 Sample을 삭제합니다.",
                    pathParameters(
                        versionPathParameter(),
                        parameterWithName("id").description("Sample 식별자"),
                    ),
                ),
            )

        coVerify(exactly = 1) { sampleService.deleteSample(1) }
    }

    private fun documentation(
        identifier: String,
        summary: String,
        description: String,
        vararg snippets: Snippet,
    ): Consumer<EntityExchangeResult<ByteArray>> = document(
        identifier = identifier,
        resourceDetails = resourceDetails()
            .tag("Samples")
            .summary(summary)
            .description(description),
        snippets = snippets,
    )

    private fun versionPathParameter() = parameterWithName("version")
        .description("API 버전 (현재 $API_VERSION_V1)")

    private fun sampleRequestFields(type: Class<*>): Array<FieldDescriptor> {
        val fields = ConstrainedFields(type)
        return arrayOf(
            fields.withPath("name")
                .type(JsonFieldType.STRING)
                .description("이름, 공백일 수 없음"),
            fields.withPath("age")
                .type(JsonFieldType.NUMBER)
                .description("나이, 0 이상 200 이하"),
            fields.withPath("status")
                .withDisplayEnum<SampleStatus>("상태"),
        )
    }

    private fun sampleResponseFields(prefix: String = ""): Array<FieldDescriptor> = arrayOf(
        fieldWithPath("${prefix}id")
            .type(JsonFieldType.NUMBER)
            .description("Sample 식별자"),
        fieldWithPath("${prefix}name")
            .type(JsonFieldType.STRING)
            .description("이름"),
        fieldWithPath("${prefix}age")
            .type(JsonFieldType.NUMBER)
            .description("나이"),
        fieldWithPath("${prefix}status")
            .withDisplayEnum<SampleStatus>("상태"),
    )

    @Configuration(proxyBeanMethods = false)
    @EnableWebFlux
    class TestConfiguration : WebFluxConfigurer {
        private val messageSource = ResourceBundleMessageSource().apply {
            setBasenames(
                "messages/message",
                "validations/validation",
                "enums/enum",
                "errors/error",
            )
            setDefaultEncoding("UTF-8")
            setFallbackToSystemLocale(false)
        }
        private val validator = LocalValidatorFactoryBean().apply {
            setValidationMessageSource(messageSource)
            afterPropertiesSet()
        }

        @Bean
        fun messageSource(): MessageSource = messageSource

        @Bean
        fun messageConverter(): MessageConverter = MessageConverter(messageSource)

        @Bean
        fun traceIdWebFilter(): TraceIdWebFilter = TraceIdWebFilter()

        @Bean
        fun traceLoggingConfiguration(): TraceLoggingConfiguration = TraceLoggingConfiguration()

        @Bean
        fun sampleService(): SampleService = mockk()

        @Bean
        fun sampleHandler(sampleService: SampleService): SampleHandler = SampleHandler(sampleService, validator)

        @Bean
        fun sampleRoutes(sampleHandler: SampleHandler) = SampleRouter(sampleHandler).sampleRoutes()

        override fun getValidator(): org.springframework.validation.Validator = validator

        override fun configureApiVersioning(configurer: ApiVersionConfigurer) {
            configurer
                .usePathSegment(1)
                .setVersionRequired(true)
        }
    }

    companion object {
        private const val DOCUMENTATION_PORT = 18080
        private const val MODIFIED_BY_HEADER = "X-Modified-By"
    }
}
