package com.example.skeleton.common.exception

import com.example.skeleton.common.errors.ErrorCode

abstract class DefaultException(
    val errorCode: ErrorCode,
    val messageArguments: Array<Any> = emptyArray(),
    val details: Map<Any, Any> = emptyMap(),
    cause: Throwable? = null,
) : RuntimeException(errorCode.getMessage(messageArguments), cause) {
    override val message: String
        get() = super.message ?: ""
}
