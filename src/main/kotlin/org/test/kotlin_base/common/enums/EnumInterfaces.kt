package org.test.kotlin_base.common.enums

import org.test.kotlin_base.common.utils.MessageConverter
import java.util.Locale

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
