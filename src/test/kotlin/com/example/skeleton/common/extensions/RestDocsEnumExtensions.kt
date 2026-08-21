package com.example.skeleton.common.extensions

import com.example.skeleton.common.enums.DisplayEnum
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.snippet.Attributes.key

inline fun <reified E> FieldDescriptor.withDisplayEnum(
    description: String,
): FieldDescriptor where E : Enum<E>, E : DisplayEnum {
    val values = E::class.displayableValues<E>().map { it.name }
    return type("enum")
        .attributes(key("enumValues").value(values))
        .description("$description ${E::class.toDocument<E>()}")
}

inline fun <reified E> ParameterDescriptor.withDisplayEnum(
    description: String,
): ParameterDescriptor where E : Enum<E>, E : DisplayEnum {
    val values = E::class.displayableValues<E>().map { it.name }
    return attributes(key("enumValues").value(values))
        .description("$description ${E::class.toDocument<E>()}")
}
