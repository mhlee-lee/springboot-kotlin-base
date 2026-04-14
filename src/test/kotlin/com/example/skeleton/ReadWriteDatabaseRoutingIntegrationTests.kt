package com.example.skeleton

import com.example.skeleton.common.constant.CommonConstant.API_VERSION_V1
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import javax.sql.DataSource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReadWriteDatabaseRoutingIntegrationTests {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    @Qualifier("writeDataSource")
    private lateinit var writeDataSource: DataSource

    @Autowired
    @Qualifier("readDataSource")
    private lateinit var readDataSource: DataSource

    private lateinit var webTestClient: WebTestClient
    private lateinit var writeJdbcTemplate: JdbcTemplate
    private lateinit var readJdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://127.0.0.1:$port")
            .build()

        writeJdbcTemplate = JdbcTemplate(writeDataSource)
        readJdbcTemplate = JdbcTemplate(readDataSource)

        seedSampleData()
        seedAddressScopeData()
    }

    @Test
    fun `sample endpoint reads from read database`() {
        webTestClient.get()
            .uri("/sample/$API_VERSION_V1/sample")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$[0].name").isEqualTo("reader-only")
            .jsonPath("$[0].age").isEqualTo(22)
            .jsonPath("$[1]").doesNotExist()
    }

    @Test
    fun `address scope endpoint reads from read database`() {
        webTestClient.get()
            .uri("/sample/$API_VERSION_V1/addressScope")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$[0].id").isEqualTo("scope-read")
            .jsonPath("$[0].addressType").isEqualTo("COMMERCIAL")
            .jsonPath("$[1]").doesNotExist()
    }

    private fun seedSampleData() {
        writeJdbcTemplate.update("delete from my_table1")
        readJdbcTemplate.update("delete from my_table1")

        writeJdbcTemplate.update(
            "insert into my_table1 (name, age) values (?, ?)",
            "writer-only",
            11,
        )

        readJdbcTemplate.update(
            "insert into my_table1 (name, age) values (?, ?)",
            "reader-only",
            22,
        )
    }

    private fun seedAddressScopeData() {
        writeJdbcTemplate.update("delete from address_scopes")
        readJdbcTemplate.update("delete from address_scopes")

        writeJdbcTemplate.update(
            """
            insert into address_scopes (id, vpc_id, status, address_type, created_at, updated_at)
            values (?, ?, ?, ?, current_timestamp, current_timestamp)
            """.trimIndent(),
            "scope-write",
            "vpc-write",
            1,
            "ATEN0001",
        )

        readJdbcTemplate.update(
            """
            insert into address_scopes (id, vpc_id, status, address_type, created_at, updated_at)
            values (?, ?, ?, ?, current_timestamp, current_timestamp)
            """.trimIndent(),
            "scope-read",
            "vpc-read",
            0,
            "ATEN0002",
        )
    }
}
