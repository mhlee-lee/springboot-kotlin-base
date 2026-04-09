package org.test.kotlin_base

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.test.kotlin_base.common.constant.CommonConstant.API_VERSION_V1

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
            .expectBody().isEmpty
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
    fun `unsupported api version returns bad request`() {
        webTestClient.get()
            .uri("/hello/9.9/hello")
            .exchange()
            .expectStatus().isBadRequest
    }
}
