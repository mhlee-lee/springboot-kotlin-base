package com.example.skeleton.common.exception

import com.example.skeleton.common.errors.CommonErrorCode
import com.example.skeleton.common.errors.ErrorSource
import org.springframework.http.HttpStatus

class UnauthorizedException : DefaultException(HttpStatus.UNAUTHORIZED, CommonErrorCode.UNAUTHORIZED)

class InvalidTokenException : DefaultException(HttpStatus.UNAUTHORIZED, CommonErrorCode.INVALID_TOKEN)

class PermissionDeniedException : DefaultException(HttpStatus.FORBIDDEN, CommonErrorCode.FORBIDDEN)

class QueryParameterBindingException(field: String, cause: Throwable? = null) :
    InvalidRequestFieldException(
        source = ErrorSource.QUERY,
        field = field,
        errorCode = CommonErrorCode.INVALID_PARAMETER,
        cause = cause,
    )
