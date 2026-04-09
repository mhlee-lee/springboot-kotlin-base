package org.test.kotlin_base.common.extensions

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bindAndAwait

suspend inline fun <reified T : Any> ServerRequest.bindQueryParams(): T =
    bindAndAwait<T>() ?: error("Failed to bind query parameters to ${T::class.qualifiedName}")

suspend inline fun <reified T : Any> ServerRequest.bindQueryParams(
    noinline dataBinderCustomizer: (org.springframework.web.bind.WebDataBinder) -> Unit,
): T = bindAndAwait<T>(dataBinderCustomizer)
    ?: error("Failed to bind query parameters to ${T::class.qualifiedName}")
