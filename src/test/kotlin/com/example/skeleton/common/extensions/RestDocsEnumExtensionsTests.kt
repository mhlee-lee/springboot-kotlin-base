package com.example.skeleton.common.extensions

import com.example.skeleton.common.enums.DisplayEnum
import com.example.skeleton.common.utils.MessageConverter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import kotlin.test.assertEquals

class RestDocsEnumExtensionsTests {
    @BeforeEach
    fun setUp() {
        MessageConverter(
            ResourceBundleMessageSource().apply {
                setBasename("enums/enum")
                setDefaultEncoding("UTF-8")
            },
        )
    }

    @Test
    fun `documents only displayable enum values with resolved messages`() {
        val field = fieldWithPath("status").withDisplayEnum<DocumentationStatus>("상태")
        val parameter = parameterWithName("status").withDisplayEnum<DocumentationStatus>("상태")

        assertEquals("enum", field.type)
        assertEquals(listOf("FIRST", "SECOND"), field.attributes["enumValues"])
        assertEquals(listOf("FIRST", "SECOND"), parameter.attributes["enumValues"])
        assertEquals("상태 [FIRST: 활성, SECOND: 정지]", field.description)
        assertEquals("상태 [FIRST: 활성, SECOND: 정지]", parameter.description)
    }

    private enum class DocumentationStatus(
        override val label: String,
        override val priority: Int,
        override val displayable: Boolean,
    ) : DisplayEnum {
        SECOND("enum.SampleStatus.SUSPENDED", 2, true),
        FIRST("enum.SampleStatus.ACTIVE", 1, true),
        HIDDEN("enum.SampleStatus.INACTIVE", 3, false),
    }
}
