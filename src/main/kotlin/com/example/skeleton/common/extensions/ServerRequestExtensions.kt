package com.example.skeleton.common.extensions

import com.example.skeleton.common.exception.RequiredHeaderException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bindAndAwait

suspend inline fun <reified T : Any> ServerRequest.bindQueryParams(): T =
    bindAndAwait<T>() ?: error("Failed to bind query parameters to ${T::class.qualifiedName}")

suspend inline fun <reified T : Any> ServerRequest.bindQueryParams(
    noinline dataBinderCustomizer: (org.springframework.web.bind.WebDataBinder) -> Unit,
): T = bindAndAwait<T>(dataBinderCustomizer)
    ?: error("Failed to bind query parameters to ${T::class.qualifiedName}")

fun ServerRequest.headerOrThrow(name: String): String =
    this.headers().firstHeader(name) ?: throw RequiredHeaderException(name)
