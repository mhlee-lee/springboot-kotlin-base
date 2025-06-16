package org.test.kotlin_base.common.extensions

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import org.test.kotlin_base.common.enums.DatePatternEnum
import org.test.kotlin_base.common.objectMapper
import java.math.BigDecimal
import java.security.InvalidParameterException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


inline fun <reified T : Any> ServerRequest.paramToCollection(name: String): Sequence<T> {
    return queryParams()[name]?.asSequence()
        ?.map { it.split(",") }
        ?.flatten()
        ?.filterNot { it.isBlank() || it == "null" }
        ?.mapNotNull { it.toModelOrNull<T>() }
        ?: emptySequence()
}

inline fun <reified T : Any, reified C : Collection<T>> ServerRequest.extractCollection(name: String): C {
    return when (C::class) {
        Set::class -> paramToCollection<T>(name).toSet() as C
        else -> paramToCollection<T>(name).toList() as C
    }
}

inline fun <reified T : Any, reified C : Collection<T>> ServerRequest.extractCollectionOrThrow(name: String): C {
    return when (C::class) {
        Set::class -> paramToCollection<T>(name).toSet() as C
        else -> paramToCollection<T>(name).toList() as C
    }.also {
        if (it.isEmpty()) throw InvalidParameterException(name)
    }
}

inline fun <reified T> ServerRequest.bindQueryParamsToModel() {
    val queryParams: MultiValueMap<String, String> = this.queryParams()
    val beanWrapper = BeanWrapperImpl(T::class.java)

    queryParams.forEach { (key, values) ->
        if (beanWrapper.isWritableProperty(key) && !values.isEmpty()) {
            beanWrapper.setPropertyValue(key, values[0]);
        }
    }
}

val requestParamToModelMapper: ObjectMapper = objectMapper.copy().apply {
    enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
}

// model to URI, for ducument
fun getUriByModel(path: String, model: Any): String {
    return UriComponentsBuilder.fromPath(path).appendQueryParam(model).build().toUriString()
}

private fun <T : UriBuilder> T.appendQueryParam(model: Any, prefixName: String? = null): T {
    val propertyNameValueMap = model::class.memberProperties.associate {
        it.isAccessible = true
        it.name to it.getter.call(model)
    }

    propertyNameValueMap.forEach { (_name, value) ->
        val name = prefixName?.let { "$it.$_name" } ?: _name
        when (value) {
            null, is Int, is Long, is String, is BigDecimal, is Boolean, is Enum<*> -> {
                queryParam(name, value)
            }
            is LocalDateTime -> queryParam(name, value.toString(DatePatternEnum.DATETIME_DEFAULT))
            is LocalDate -> queryParam(name, value.toString(DatePatternEnum.DATE_DEFAULT))
            is Collection<*> -> queryParam(name, value.joinToString(","))
            else -> {
                println("value: $value")
                println("name: $name")
                this.appendQueryParam(value, name)
            }
        }
    }

    return this
}

fun ServerRequest.header(name: String): String? {
    return this.headers().firstHeader(name)
}
