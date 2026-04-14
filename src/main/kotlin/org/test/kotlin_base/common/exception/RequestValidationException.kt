package org.test.kotlin_base.common.exception

import org.springframework.http.HttpStatus
import org.test.kotlin_base.common.errors.ApiFieldError
import org.test.kotlin_base.common.errors.ErrorCode

class RequestValidationException(
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
    val errorCode: ErrorCode,
    val fieldErrors: List<ApiFieldError>,
    cause: Throwable? = null,
) : RuntimeException(errorCode.getMessage(), cause) {
    companion object {
        fun single(
            status: HttpStatus = HttpStatus.BAD_REQUEST,
            errorCode: ErrorCode,
            fieldError: ApiFieldError,
            cause: Throwable? = null,
        ): RequestValidationException {
            return RequestValidationException(status, errorCode, listOf(fieldError), cause)
        }
    }
}
