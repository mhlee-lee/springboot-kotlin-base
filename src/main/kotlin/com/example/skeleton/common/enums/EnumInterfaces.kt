package com.example.skeleton.common.enums

import com.example.skeleton.common.utils.MessageConverter
import java.util.*

// for database
interface GenericEnum {
    val value: String
}

// for presentation layer
interface DisplayEnum {
    val label: String
    val priority: Int
    val displayable: Boolean

    fun getMessage() = MessageConverter.getMessage(label)
    fun getMessage(locale: Locale) = MessageConverter.getMessage(label, locale)
}
