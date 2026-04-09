package org.test.kotlin_base.common.exception

import org.test.kotlin_base.common.errors.ErrorCode

abstract class DefaultException(
    val errorCode: ErrorCode,
    val messageArguments: Array<Any> = emptyArray(),
    val details: Map<Any, Any> = emptyMap(),
    cause: Throwable? = null,
) : RuntimeException(errorCode.getMessage(messageArguments), cause) {
    override val message: String
        get() = super.message ?: ""
}
