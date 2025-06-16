package org.test.kotlin_base.common.utils

import org.springframework.context.support.ResourceBundleMessageSource
import org.test.kotlin_base.common.constant.CommonConstant.DEFAULT_LOCALE
import java.nio.charset.StandardCharsets
import java.util.*

object MessageConverter {
    fun getMessage(code: String, locale: Locale = DEFAULT_LOCALE) = message.getMessage(code, null, locale)
    fun getMessage(code: String, args: Array<Any>?, locale: Locale = DEFAULT_LOCALE) = message.getMessage(code, args, locale)

    val message = ResourceBundleMessageSource().apply {
        setBasenames(
            "messages/message",
            "validations/validation",
            "enums/enum",
            "errors/error",
        )
        setDefaultEncoding(StandardCharsets.UTF_8.name())
    }
}
