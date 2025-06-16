package org.test.kotlin_base.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.test.kotlin_base.common.errors.CommonErrorCode

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException : DefaultException(CommonErrorCode.UNAUTHORIZED)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidTokenException : DefaultException(CommonErrorCode.INVALID_TOKEN)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidHeaderParameterException(parameter: String) :
    DefaultException(CommonErrorCode.INVALID_HEADER_PARAMETER, arrayOf(parameter))

@ResponseStatus(HttpStatus.FORBIDDEN)
class PermissionDeniedException : DefaultException(CommonErrorCode.FORBIDDEN)
