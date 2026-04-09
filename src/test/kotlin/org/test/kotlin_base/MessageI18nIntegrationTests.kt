package org.test.kotlin_base

import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.test.kotlin_base.common.utils.MessageConverter
import kotlin.test.assertEquals

@SpringBootTest
class MessageI18nIntegrationTests(
    @Autowired private val applicationValidator: LocalValidatorFactoryBean,
) {
    @Test
    fun `message converter delegates to spring message source`() {
        assertEquals("남성", MessageConverter.getMessage("enum.Gender.MALE"))
    }

    @Test
    fun `validator uses configured message source`() {
        val target = ValidationPayload(name = "")
        val errors = BeanPropertyBindingResult(target, "validationPayload")

        applicationValidator.validate(target, errors)

        assertEquals("필수 입력값입니다.", errors.getFieldError("name")?.defaultMessage)
    }

    private data class ValidationPayload(
        @field:NotBlank
        val name: String,
    )
}
