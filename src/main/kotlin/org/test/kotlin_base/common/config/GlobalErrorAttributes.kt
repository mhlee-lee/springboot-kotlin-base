package org.test.kotlin_base.common.config

import jakarta.validation.ConstraintViolationException
import jakarta.validation.constraints.*
import org.slf4j.LoggerFactory
import org.springframework.boot.json.JsonParseException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.webflux.error.DefaultErrorAttributes
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
import tools.jackson.databind.exc.InvalidFormatException
import tools.jackson.databind.exc.MismatchedInputException
import java.time.LocalDateTime
import java.util.*

class GlobalErrorAttributes(private val messageResolver: MessageCodesResolver) : DefaultErrorAttributes() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): Map<String, Any> {
        val attributes = mutableMapOf<String, Any>()

        val error = getError(request)
        val locale = request.exchange().localeContext.locale ?: DEFAULT_LOCALE
        val errorResult = getErrorResult(error, locale)

        attributes["timestamp"] = LocalDateTime.now()
        attributes["path"] = "${request.method()} ${request.path()}"
        attributes["status"] = errorResult.status.value()
        attributes["code"] = errorResult.code
        attributes["message"] = errorResult.message
        errorResult.errors?.let { attributes["errors"] = it }

        return attributes
    }

    // 순서 중요
    private fun getErrorResult(error: Throwable, locale: Locale): ErrorResponse {
        return when (error) {
            is DefaultException -> handleDefaultException(error, locale)
            is WebExchangeBindException -> handleWebExchangeBindException(error, locale)
            is ConstraintViolationException -> handleConstraintViolationException(error, locale)
            is ServerWebInputException -> handleServerWebInputException(error, locale)
            is InvalidMediaTypeException -> handleInvalidMediaTypeException(locale)
            is DataBufferLimitException -> handleDataBufferLimitException(locale)
            is ResponseStatusException -> handleResponseStatusException(error, locale)
            else -> handleUnknownException(locale)
        }
    }

    private fun handleDefaultException(ex: DefaultException, locale: Locale): ErrorResponse {
        val httpStatus = AnnotatedElementUtils.findMergedAnnotation(ex.javaClass, ResponseStatus::class.java)
            ?.code
            ?: HttpStatus.BAD_REQUEST

        return ErrorResponse(
            httpStatus,
            ex.errorCode.code,
            ex.errorCode.getMessage(ex.messageArguments, locale),
            ex.details.takeIf { ex.details.isNotEmpty() }
        )
    }

    // Spring Web Validation 관련 처리
    private fun handleWebExchangeBindException(ex: WebExchangeBindException, locale: Locale): ErrorResponse {
        val validationMessageResults = ex.bindingResult.fieldErrors.associate { fieldError ->
            val message = fieldError.codes
                ?.firstNotNullOfOrNull { code ->
                    MessageConverter.getMessageOrNull(code, fieldError.arguments, locale)
                }
                ?: fieldError.defaultMessage
                ?: "Invalid value"

            fieldError.field to message
        }

        return ErrorResponse(
            HttpStatus.BAD_REQUEST,
            CommonErrorCode.INVALID_PARAMETER.code,
            CommonErrorCode.INVALID_PARAMETER.getMessage(locale = locale),
            validationMessageResults
        )
    }

    private fun handleServerWebInputException(ex: ServerWebInputException, locale: Locale): ErrorResponse {
        val httpStatus = HttpStatus.BAD_REQUEST
        var errorCode: ErrorCode
        var errors: Any? = null

        when (val rootCause = ex.cause) {
            is InvalidFormatException -> {
                errorCode = CommonErrorCode.INVALID_FORMAT
                errors = rootCause.path.associate { reference ->
                    reference.propertyName to (reference.description ?: "Invalid format")
                }
            }
            is MismatchedInputException -> {
                errorCode = CommonErrorCode.MISMATCH
                errors = rootCause.path.associate { reference ->
                    reference.propertyName to (reference.description ?: "Invalid format")
                }
            }
            is JsonParseException -> {
                errorCode = CommonErrorCode.JSON_PARSE_ERROR
            }
            else -> {
                errorCode = CommonErrorCode.BAD_REQUEST
            }
        }

        return ErrorResponse(httpStatus, errorCode.code, errorCode.getMessage(locale = locale), errors)
    }

    private fun handleResponseStatusException(ex: ResponseStatusException, locale: Locale): ErrorResponse {
        return ErrorResponse(
            ex.statusCode as HttpStatus,
            code = CommonErrorCode.BAD_REQUEST.code,
            message = CommonErrorCode.BAD_REQUEST.getMessage(locale = locale)
        )
    }

    // Bean Validation 실패
    private fun handleConstraintViolationException(ex: ConstraintViolationException, locale: Locale): ErrorResponse {
        val errors = ex.constraintViolations.mapNotNull { violation ->
            val property = violation.propertyPath.toString()
            val constraint = violation.constraintDescriptor.annotation.annotationClass.simpleName ?: "Unknown"
            val objectName = violation.rootBeanClass?.simpleName?.lowercase() ?: "object"

            val messageCode = messageResolver.resolveMessageCodes(constraint, objectName, property, violation.leafBean.javaClass)
                .firstOrNull()

            if (messageCode != null) {
                val args = extractConstraintArguments(violation.constraintDescriptor.annotation)
                property to MessageConverter.getMessage(
                    code = messageCode,
                    args = args,
                    locale = locale,
                    defaultMessage = violation.message ?: property,
                )
            } else null
        }.toMap()

        return ErrorResponse(
            HttpStatus.BAD_REQUEST,
            CommonErrorCode.VALIDATION_FAIL.code,
            CommonErrorCode.VALIDATION_FAIL.getMessage(locale = locale),
            errors
        )
    }

    private fun handleInvalidMediaTypeException(locale: Locale): ErrorResponse {
        return ErrorResponse(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.code,
            CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage(locale = locale)
        )
    }

    private fun handleDataBufferLimitException(locale: Locale): ErrorResponse {
        return ErrorResponse(
            HttpStatus.PAYLOAD_TOO_LARGE,
            CommonErrorCode.PAYLOAD_TOO_LARGE.code,
            CommonErrorCode.PAYLOAD_TOO_LARGE.getMessage(locale = locale)
        )
    }

    private fun handleUnknownException(locale: Locale) = ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        code = CommonErrorCode.INTERNAL_SERVER_ERROR.code,
        message = CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage(locale = locale)
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

    private class ErrorResponse(
        val status: HttpStatus,
        val code: String,
        val message: String = "",
        val errors: Any? = null
    )
}
