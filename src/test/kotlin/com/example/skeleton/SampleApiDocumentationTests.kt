package com.example.skeleton

import com.epages.restdocs.apispec.ConstrainedFields
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.document
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.resourceDetails
import com.example.skeleton.common.constant.CommonConstant.API_VERSION_V1
import com.example.skeleton.common.extensions.withDisplayEnum
import com.example.skeleton.domain.sample.model.SampleStatus
import com.example.skeleton.presentaion.protocol.CreateSampleRequest
import com.example.skeleton.presentaion.protocol.UpdateSampleRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
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
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.function.Consumer
import javax.sql.DataSource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SampleApiDocumentationTests {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    @Qualifier("writeDataSource")
    private lateinit var writeDataSource: DataSource

    @Autowired
    @Qualifier("readDataSource")
    private lateinit var readDataSource: DataSource

    private lateinit var webTestClient: WebTestClient
    private lateinit var writeJdbc: JdbcTemplate
    private lateinit var readJdbc: JdbcTemplate

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val restDocsConfigurer = documentationConfiguration(restDocumentation)
        restDocsConfigurer.operationPreprocessors()
            .withRequestDefaults(
                modifyUris().scheme("http").host("localhost").port(DOCUMENTATION_PORT),
                prettyPrint(),
            )
            .withResponseDefaults(prettyPrint())

        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://127.0.0.1:$port")
            .filter(restDocsConfigurer)
            .build()
        writeJdbc = JdbcTemplate(writeDataSource)
        readJdbc = JdbcTemplate(readDataSource)
        writeJdbc.update("DELETE FROM samples")
        readJdbc.update("DELETE FROM samples")
    }

    @Test
    fun `documents searching samples`() {
        readJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (1, 'Alice', 30, 'active')")
        readJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (2, 'Bob', 20, 'inactive')")

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
    }

    @Test
    fun `documents searching samples by status`() {
        readJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (1, 'Alice', 30, 'active')")
        readJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (2, 'Bob', 20, 'inactive')")

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
    }

    @Test
    fun `documents getting a sample`() {
        readJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (1, 'Bob', 25, 'active')")

        webTestClient.get()
            .uri("/sample/{version}/samples/{id}", API_VERSION_V1, 1)
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
    }

    @Test
    fun `documents creating a sample`() {
        val request = CreateSampleRequest(
            name = "Alice",
            age = 30,
            status = SampleStatus.ACTIVE,
        )

        webTestClient.post()
            .uri("/sample/{version}/samples", API_VERSION_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
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
    }

    @Test
    fun `documents updating a sample`() {
        writeJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (1, 'Before', 20, 'active')")
        val request = UpdateSampleRequest(
            name = "After",
            age = 31,
            status = SampleStatus.INACTIVE,
        )

        webTestClient.put()
            .uri("/sample/{version}/samples/{id}", API_VERSION_V1, 1)
            .header(MODIFIED_BY_HEADER, "document-writer")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
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
    }

    @Test
    fun `documents deleting a sample`() {
        writeJdbc.update("INSERT INTO samples (id, name, age, status) VALUES (1, 'Delete', 20, 'active')")

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

    companion object {
        private const val DOCUMENTATION_PORT = 18080
        private const val MODIFIED_BY_HEADER = "X-Modified-By"
    }
}
