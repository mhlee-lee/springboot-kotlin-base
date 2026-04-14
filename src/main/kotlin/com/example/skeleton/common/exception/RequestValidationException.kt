package com.example.skeleton.common.exception

import com.example.skeleton.common.errors.ApiFieldError
import com.example.skeleton.common.errors.ErrorCode
import org.springframework.http.HttpStatus

open class RequestValidationException(
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
    val errorCode: ErrorCode,
    val fieldErrors: List<ApiFieldError>,
    cause: Throwable? = null,
) : RuntimeException(errorCode.getMessage(), cause)
