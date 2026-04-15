package com.example.skeleton.common.exception

import com.example.skeleton.common.errors.ApiFieldError
import com.example.skeleton.common.errors.ErrorCode
import org.springframework.http.HttpStatus

open class RequestValidationException(
    status: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode,
    val fieldErrors: List<ApiFieldError>,
    cause: Throwable? = null,
) : DefaultException(status = status, errorCode = errorCode, cause = cause)
