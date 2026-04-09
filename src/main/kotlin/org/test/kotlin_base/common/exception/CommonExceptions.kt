package org.test.kotlin_base.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.test.kotlin_base.common.errors.CommonErrorCode

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException : DefaultException(CommonErrorCode.UNAUTHORIZED)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidTokenException : DefaultException(CommonErrorCode.INVALID_TOKEN)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidHeaderParameterException(parameter: String) :
    DefaultException(CommonErrorCode.INVALID_HEADER_PARAMETER, arrayOf(parameter))

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequestParameterException(parameter: String) :
    DefaultException(CommonErrorCode.INVALID_PARAMETER, arrayOf(parameter))

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EmptyBodyException : DefaultException(CommonErrorCode.EMPTY_BODY)

@ResponseStatus(HttpStatus.FORBIDDEN)
class PermissionDeniedException : DefaultException(CommonErrorCode.FORBIDDEN)
