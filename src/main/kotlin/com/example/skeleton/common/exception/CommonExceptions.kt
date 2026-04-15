package com.example.skeleton.common.exception

import com.example.skeleton.common.errors.CommonErrorCode
import org.springframework.http.HttpStatus

class UnauthorizedException : DefaultException(HttpStatus.UNAUTHORIZED, CommonErrorCode.UNAUTHORIZED)

class InvalidTokenException : DefaultException(HttpStatus.UNAUTHORIZED, CommonErrorCode.INVALID_TOKEN)

class PermissionDeniedException : DefaultException(HttpStatus.FORBIDDEN, CommonErrorCode.FORBIDDEN)
