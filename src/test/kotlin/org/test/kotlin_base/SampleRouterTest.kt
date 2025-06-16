package org.test.kotlin_base

import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.snippet.Attributes
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.test.kotlin_base.presentation.enums.Gender
import org.test.kotlin_base.presentation.sample.SampleHandler
import org.test.kotlin_base.presentation.sample.SampleRouter
import org.test.kotlin_base.presentation.sample.protocol.SampleRequest
import org.test.kotlin_base.presentation.sample.protocol.SampleResponse

@WebFluxTest
@AutoConfigureRestDocs
@ContextConfiguration(classes = [SampleRouter::class, SampleHandler::class])
class SampleRouterTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    @Throws(Exception::class)
    fun putTest() {
        val request = SampleRequest("example-id", 10)
        val response = SampleResponse("example-name", 999, Gender.FEMALE, "example-id", 10)

        webTestClient.put().uri("/put-test/{gender}?name={name}", Gender.FEMALE.name, response.name)
            .header("age", response.age.toString())
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith { response ->
                document<>()
                document(
                    "put-test",

                    response = response,
                    responseType = SampleResponse::class.java,

                    snippets = ResourceDocumentation.resource(
                    )
                )

            }


        mockMvc.perform(
            put("/put-test/{gender}", Gender.FEMAIL.name())
                .header("customKey", response.header())
                .queryParam("age", response.ageParam().toString())
                .queryParam("name", response.nameParam())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JacksonUtil.toJsonOrEmptyString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "put-test",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("put-test-tag")
                            .description("Put Test Description")
                            .requestHeaders(
                                headerWithName("customKey").type(SimpleType.STRING).description("성별")
                                    .attributes(Attributes.key("example").value(response.gender() + "-docs"))
                                    .optional()
                            )
                            .pathParameters(
                                parameterWithName("gender").type(SimpleType.STRING)
                                    .description(EnumDocUtils.enumDescriptionDocs("성별", Gender::class.java))
                                    .attributes(
                                        Attributes.key("enumValues").value(EnumDocUtils.enumNames(Gender::class.java))
                                    )
                            )
                            .queryParameters(
                                parameterWithName("age").type(SimpleType.NUMBER)
                                    .description("나이")
                                    .attributes(
                                        Attributes.key("example").value(response.ageParam() + "-docs")
                                    ).optional(),
                                parameterWithName("name").type(SimpleType.STRING).description("이름")
                                    .attributes(Attributes.key("example").value(response.nameParam() + "-docs"))
                            )
                            .requestFields(
                                PayloadDocumentation.fieldWithPath("id").type(JsonFieldType.STRING).description("아이디"),
                                PayloadDocumentation.fieldWithPath("ttl").type(JsonFieldType.NUMBER)
                                    .description("time to live")
                            )
                            .responseFields(
                                PayloadDocumentation.fieldWithPath("id").type(JsonFieldType.STRING).description("아이디"),
                                PayloadDocumentation.fieldWithPath("ttl").type(JsonFieldType.NUMBER)
                                    .description("time to live").optional(),
                                PayloadDocumentation.fieldWithPath("gender").type(JsonFieldType.STRING)
                                    .description(EnumDocUtils.enumDescriptionDocs("성별", Gender::class.java))
                                    .attributes(
                                        Attributes.key("enumValues").value(EnumDocUtils.enumNames(Gender::class.java))
                                    ),
                                PayloadDocumentation.fieldWithPath("header").type(JsonFieldType.STRING)
                                    .description("헤더 요청값"),
                                PayloadDocumentation.fieldWithPath("ageParam").type(JsonFieldType.NUMBER)
                                    .description("나이 파라메터 요청값"),
                                PayloadDocumentation.fieldWithPath("nameParam").type(JsonFieldType.STRING)
                                    .description("이름 파라메터 요청값")
                            )
                            .build()
                    )
                )
            )
    }
}
