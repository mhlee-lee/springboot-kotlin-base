package org.test.kotlin_base.common.utils

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.test.kotlin_base.common.constant.CommonConstant.DEFAULT_LOCALE
import java.util.*

object MessageConverter {
    @Volatile
    private lateinit var messageSource: MessageSource

    internal fun initialize(messageSource: MessageSource) {
        this.messageSource = messageSource
    }

    fun getMessage(code: String, locale: Locale = DEFAULT_LOCALE): String =
        getMessage(code, null, locale, code)

    fun getMessage(
        code: String,
        args: Array<Any>?,
        locale: Locale = DEFAULT_LOCALE,
        defaultMessage: String = code,
    ): String = getMessageOrNull(code, args, locale) ?: defaultMessage

    fun getMessageOrNull(
        code: String,
        args: Array<Any>? = null,
        locale: Locale = DEFAULT_LOCALE,
    ): String? = messageSource.getMessage(code, args, null, locale)
}

@Component
class MessageConverterInitializer(messageSource: MessageSource) {
    init {
        MessageConverter.initialize(messageSource)
    }
}
