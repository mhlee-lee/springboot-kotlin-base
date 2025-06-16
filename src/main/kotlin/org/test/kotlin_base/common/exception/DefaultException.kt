package org.test.kotlin_base.common.exception

import org.test.kotlin_base.common.errors.ErrorCode

abstract class DefaultException(
    val errorCode: ErrorCode,
    val args: Array<Any>? = null,
    val details: Map<Any, Any> = emptyMap(),
) : RuntimeException() {
    override val message: String
        get() = errorCode.getMessage()
}
