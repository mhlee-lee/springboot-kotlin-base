package org.test.kotlin_base.common.errors

import org.test.kotlin_base.common.constant.CommonConstant
import org.test.kotlin_base.common.utils.MessageConverter
import java.util.Locale

interface ErrorCode {
    val code: String
    val label: String

    fun getMessage(args: Array<Any>? = null) = MessageConverter.getMessage(label, args, CommonConstant.DEFAULT_LOCALE)
    fun getMessage(args: Array<Any>? = null, locale: Locale) = MessageConverter.getMessage(label, args, locale)
}
