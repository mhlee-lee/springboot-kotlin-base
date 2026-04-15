package com.example.skeleton.common.extensions

import com.example.skeleton.common.exception.QueryParameterBindingException
import com.example.skeleton.common.exception.RequiredHeaderException
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.validation.BindException
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.reactive.function.server.ServerRequest
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

suspend inline fun <reified T : Any> ServerRequest.bindQueryParams(): T = bindQueryParams(T::class)

suspend inline fun <reified T : Any> ServerRequest.bindQueryParams(
    noinline dataBinderCustomizer: (WebDataBinder) -> Unit,
): T = bindQueryParams(T::class, dataBinderCustomizer)

fun ServerRequest.headerOrThrow(name: String): String =
    this.headers().firstHeader(name) ?: throw RequiredHeaderException(name)

suspend fun <T : Any> ServerRequest.bindQueryParams(clazz: KClass<T>): T = bindQueryParams(clazz) { }

suspend fun <T : Any> ServerRequest.bindQueryParams(
    clazz: KClass<T>,
    dataBinderCustomizer: (WebDataBinder) -> Unit,
): T = try {
    bind(clazz.java, dataBinderCustomizer).awaitSingleOrNull()
        ?: throw QueryParameterBindingException(resolveQueryField(clazz))
} catch (exception: BindException) {
    throw QueryParameterBindingException(resolveBindField(exception), exception)
} catch (exception: IllegalStateException) {
    throw QueryParameterBindingException(resolveQueryField(clazz), exception)
}

private fun resolveBindField(exception: BindException): String =
    exception.bindingResult.fieldErrors.firstOrNull()?.field
        ?: exception.bindingResult.globalErrors.firstOrNull()?.objectName
        ?: "query"

private fun <T : Any> ServerRequest.resolveQueryField(clazz: KClass<T>): String {
    val queryFields = queryParams().keys
    val constructorFields = clazz.primaryConstructor
        ?.parameters
        ?.filter { parameter -> !parameter.isOptional && !parameter.type.isMarkedNullable }
        ?.mapNotNull(KParameter::name)
        .orEmpty()

    return constructorFields.firstOrNull { it !in queryFields }
        ?: queryFields.firstOrNull()
        ?: clazz.primaryConstructor?.parameters?.firstNotNullOfOrNull(KParameter::name)
        ?: clazz.simpleName
        ?: "query"
}
