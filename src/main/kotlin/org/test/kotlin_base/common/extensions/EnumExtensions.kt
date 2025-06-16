package org.test.kotlin_base.common.extensions

import org.test.kotlin_base.common.enums.DisplayEnum
import kotlin.reflect.KClass

inline fun <reified E> byLabel(label: String): E? where E : Enum<E>, E: DisplayEnum {
    return enumValues<E>().firstOrNull { it.label == label }
}

inline fun <reified T> KClass<T>.toDocument(): String where T: Enum<T>, T: DisplayEnum {
    return enumValues<T>().filter { it.displayable }
        .sortedBy { it.priority }
        .joinToString(separator = ",", prefix = "[", postfix = "]") {
            "${it.name}: ${it.label}"
        }
}
