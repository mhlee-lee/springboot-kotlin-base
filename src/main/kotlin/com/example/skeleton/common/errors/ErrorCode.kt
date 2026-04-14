package com.example.skeleton.common.errors

import com.example.skeleton.common.constant.CommonConstant
import com.example.skeleton.common.utils.MessageConverter
import java.util.*

interface ErrorCode {
    val code: String
    val label: String

    fun getMessage(args: Array<Any>? = null) =
        MessageConverter.getMessage(label, args, CommonConstant.DEFAULT_LOCALE, label)

    fun getMessage(args: Array<Any>? = null, locale: Locale) = MessageConverter.getMessage(label, args, locale, label)
}
