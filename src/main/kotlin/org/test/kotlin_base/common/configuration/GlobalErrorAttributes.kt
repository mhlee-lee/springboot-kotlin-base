package org.test.kotlin_base.common.configuration

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.validation.ConstraintViolationException
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.io.buffer.DataBufferLimitException
import org.springframework.http.HttpStatus
import org.springframework.http.InvalidMediaTypeException
import org.springframework.validation.MessageCodesResolver
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import org.test.kotlin_base.common.constant.CommonConstant.DEFAULT_LOCALE
import org.test.kotlin_base.common.errors.CommonErrorCode
import org.test.kotlin_base.common.errors.ErrorCode
import org.test.kotlin_base.common.exception.DefaultException
import org.test.kotlin_base.common.utils.MessageConverter
import java.time.LocalDateTime

class GlobalErrorAttributes(private val messageResolver: MessageCodesResolver) : DefaultErrorAttributes() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): Map<String, Any> {
        val attributes = mutableMapOf<String, Any>()

        val error = getError(request)
        val errorResult = getErrorResult(error)

        attributes["timestamp"] = LocalDateTime.now()
        attributes["path"] = "${request.method()} ${request.path()}"
        attributes["status"] = errorResult.status.value()
        attributes["code"] = errorResult.code
        attributes["message"] = errorResult.message
        errorResult.errors?.let { attributes["errors"] = it }

        return attributes
    }

    // 순서 중요
    private fun getErrorResult(error: Throwable): ErrorResultResponse {
        return when (error) {
            is DefaultException -> handleDefaultException(error)
            is WebExchangeBindException -> handleWebExchangeBindException(error)
            is ConstraintViolationException -> handleConstraintViolationException(error)
            is ServerWebInputException -> handleServerWebInputException(error)
            is InvalidMediaTypeException -> handleInvalidMediaTypeException(error)
            is DataBufferLimitException -> handleDataBufferLimitException(error)
            is ResponseStatusException -> handleResponseStatusException(error)
            else -> handleUnknownException()
        }
    }

    private fun handleDefaultException(ex: DefaultException): ErrorResultResponse {
        val httpStatus = AnnotatedElementUtils.findMergedAnnotation(ex.javaClass, ResponseStatus::class.java)
            ?.code
            ?: HttpStatus.BAD_REQUEST

        return ErrorResultResponse(httpStatus, ex.errorCode.code, ex.message, ex.details.takeIf { ex.details.isNotEmpty() })
    }

    // Spring Web Validation 관련 처리
    private fun handleWebExchangeBindException(ex: WebExchangeBindException): ErrorResultResponse {
        val validationMessageResults = ex.bindingResult.fieldErrors.associate { fieldError ->
            val message = fieldError.codes
                ?.map { code ->
                    runCatching { MessageConverter.getMessage(code, fieldError.arguments, DEFAULT_LOCALE) }.getOrNull()
                }?.first { it != null }
                ?: fieldError.defaultMessage
                ?: "Invalid value"

            fieldError.field to message
        }

        return ErrorResultResponse(
            HttpStatus.BAD_REQUEST,
            CommonErrorCode.INVALID_PARAMETER.code,
            CommonErrorCode.INVALID_PARAMETER.getMessage(),
            validationMessageResults
        )
    }

    private fun handleServerWebInputException(ex: ServerWebInputException): ErrorResultResponse {
        val httpStatus = HttpStatus.BAD_REQUEST
        var errorCode: ErrorCode
        var errors: Any? = null

        when (val rootCause = ex.cause) {
            is InvalidFormatException -> {
                errorCode = CommonErrorCode.INVALID_FORMAT
                errors = rootCause.path.mapNotNull { it.fieldName to (it.description ?: "Invalid format")}
            }
            is MismatchedInputException -> {
                errorCode = CommonErrorCode.MISMATCH
                errors = rootCause.path.mapNotNull { it.fieldName to (it.description ?: "Invalid format") }
            }
            is JsonParseException -> {
                errorCode = CommonErrorCode.JSON_PARSE_ERROR
            }
            is JsonMappingException -> {
                errorCode = CommonErrorCode.JSON_PARSE_ERROR
                errors = rootCause.path.mapNotNull { it.fieldName }
            }
            else -> {
                errorCode = CommonErrorCode.BAD_REQUEST
            }
        }

        return ErrorResultResponse(httpStatus, errorCode.code, errorCode.getMessage(), errors)
    }

    private fun handleResponseStatusException(ex: ResponseStatusException): ErrorResultResponse {
        return ErrorResultResponse(
            ex.statusCode as HttpStatus,
            code = CommonErrorCode.BAD_REQUEST.code,
            message = CommonErrorCode.BAD_REQUEST.getMessage()
        )
    }

    // Bean Validation 실패
    private fun handleConstraintViolationException(ex: ConstraintViolationException): ErrorResultResponse {
        val errors = ex.constraintViolations.mapNotNull { violation ->
            val property = violation.propertyPath.toString()
            val constraint = violation.constraintDescriptor.annotation.annotationClass.simpleName ?: "Unknown"
            val objectName = violation.rootBeanClass?.simpleName?.lowercase() ?: "object"

            val messageCode = messageResolver.resolveMessageCodes(constraint, objectName, property, violation.leafBean.javaClass)
                .firstOrNull()

            if (messageCode != null) {
                val args = extractConstraintArguments(violation.constraintDescriptor.annotation)
                property to MessageConverter.getMessage(messageCode, args)
            } else null
        }.toMap()

        return ErrorResultResponse(
            HttpStatus.BAD_REQUEST,
            CommonErrorCode.VALIDATION_FAIL.code,
            CommonErrorCode.VALIDATION_FAIL.getMessage(),
            errors
        )
    }

    private fun handleInvalidMediaTypeException(ex: InvalidMediaTypeException): ErrorResultResponse {
        return ErrorResultResponse(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.code,
            CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage()
        )
    }

    private fun handleDataBufferLimitException(ex: DataBufferLimitException): ErrorResultResponse {
        return ErrorResultResponse(
            HttpStatus.PAYLOAD_TOO_LARGE,
            CommonErrorCode.PAYLOAD_TOO_LARGE.code,
            CommonErrorCode.PAYLOAD_TOO_LARGE.getMessage()
        )
    }

    private fun handleUnknownException() = ErrorResultResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        code = CommonErrorCode.INTERNAL_SERVER_ERROR.code,
        message = CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()
    )

    // validation 어노테이션에서 속성값 추출
    private fun extractConstraintArguments(annotation: Annotation): Array<Any> {
        return when (annotation) {
            is Size -> arrayOf(annotation.min, annotation.max)
            is Min -> arrayOf(annotation.value)
            is Max -> arrayOf(annotation.value)
            is DecimalMin -> arrayOf(annotation.value)
            is DecimalMax -> arrayOf(annotation.value)
            is Pattern-> arrayOf(annotation.regexp)
            else -> emptyArray()
        }
    }

    private class ErrorResultResponse(
        val status: HttpStatus,
        val code: String,
        val message: String = "",
        val errors: Any? = null
    )
}
