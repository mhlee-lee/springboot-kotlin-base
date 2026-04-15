package com.example.skeleton

import com.example.skeleton.common.errors.SampleErrorCode
import com.example.skeleton.common.utils.MessageConverter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@ActiveProfiles("test")
class MessageI18nIntegrationTests {
    @Test
    fun `message converter resolves error messages from message source`() {
        val message = MessageConverter.getMessage(SampleErrorCode.SAMPLE_NOT_FOUND.label, arrayOf(42L))
        assertNotNull(message)
        assertEquals("샘플을 찾을 수 없습니다. ID: 42", message)
    }
}
