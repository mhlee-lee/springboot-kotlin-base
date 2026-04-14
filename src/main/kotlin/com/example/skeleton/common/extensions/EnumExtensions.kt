package com.example.skeleton.common.extensions

import com.example.skeleton.common.enums.DisplayEnum
import kotlin.reflect.KClass

inline fun <reified E> byLabel(label: String): E? where E : Enum<E>, E : DisplayEnum = enumValues<E>().firstOrNull {
    it.label == label
}

inline fun <reified T> KClass<T>.toDocument(): String where T : Enum<T>, T : DisplayEnum = enumValues<T>().filter {
    it.displayable
}
    .sortedBy { it.priority }
    .joinToString(separator = ",", prefix = "[", postfix = "]") {
        "${it.name}: ${it.label}"
    }
