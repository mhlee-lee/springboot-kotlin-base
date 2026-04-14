package com.example.skeleton

import com.example.skeleton.common.utils.MessageConverter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class MessageI18nIntegrationTests(
    @Autowired private val applicationValidator: LocalValidatorFactoryBean,
) {
    @Test
    fun `message converter delegates to spring message source`() {
        assertEquals("남성", MessageConverter.getMessage("enum.Gender.MALE"))
    }
}
