package com.example.skeleton

import com.example.skeleton.common.constant.CommonConstant.API_VERSION_V1
import com.example.skeleton.common.errors.CommonErrorCode
import com.example.skeleton.common.errors.ErrorSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SampleValidationIntegrationTests {
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
    fun `validation sample endpoint echoes validated request`() {
        webTestClient.post()
            .uri("/sample/$API_VERSION_V1/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "quantity": 120,
                  "name": "tester",
                  "requiredValue": "exists",
                  "code": "ABC-12",
                  "ttl": 60
                }
                """.trimIndent(),
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.quantity").isEqualTo(120)
            .jsonPath("$.name").isEqualTo("tester")
            .jsonPath("$.requiredValue").isEqualTo("exists")
            .jsonPath("$.code").isEqualTo("ABC-12")
    }

    @Test
    fun `validation sample endpoint returns expected field errors for range size not null and pattern`() {
        webTestClient.post()
            .uri("/sample/$API_VERSION_V1/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "quantity": 0,
                  "name": "A",
                  "requiredValue": null,
                  "code": "abc-12",
                  "ttl": 60
                }
                """.trimIndent(),
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().exists("X-Trace-Id")
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.code").isEqualTo(CommonErrorCode.VALIDATION_FAIL.code)
            .jsonPath("$.path").isEqualTo("/sample/1.0/validation")
            .jsonPath("$.traceId").exists()
            .jsonPath("$.errors[0].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[0].field").isEqualTo("code")
            .jsonPath("$.errors[1].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[1].field").isEqualTo("name")
            .jsonPath("$.errors[2].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[2].field").isEqualTo("quantity")
            .jsonPath("$.errors[3].source").isEqualTo(ErrorSource.BODY.wireName)
            .jsonPath("$.errors[3].field").isEqualTo("requiredValue")
    }
}
