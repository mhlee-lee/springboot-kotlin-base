package org.test.kotlin_base.adapter.`in`.web.sample

import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.test.kotlin_base.adapter.`in`.web.sample.protocol.SampleRequest
import org.test.kotlin_base.adapter.`in`.web.sample.protocol.SampleResponse
import org.test.kotlin_base.application.port.`in`.sample.SampleUseCase
import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleCommand
import org.test.kotlin_base.common.errors.ApiFieldError
import org.test.kotlin_base.common.errors.CommonErrorCode
import org.test.kotlin_base.common.errors.ErrorSource
import org.test.kotlin_base.common.exception.RequestValidationException
import org.test.kotlin_base.common.extensions.validateOrThrow
import org.test.kotlin_base.domain.sample.model.Gender

@Component
class SampleHandler(
    private val sampleUseCase: SampleUseCase,
    private val validator: Validator,
) {
    suspend fun getSample(request: ServerRequest): ServerResponse {
        sampleUseCase.getSamples()
        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun addressScope(request: ServerRequest): ServerResponse {
        sampleUseCase.getAddressScopes()
        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun putSample(request: ServerRequest): ServerResponse {
        val age = request.headers().firstHeader("age")?.toIntOrNull()
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.INVALID_HEADER_PARAMETER,
                fieldError = fieldError(
                    source = ErrorSource.HEADER,
                    field = "age",
                    reason = if (request.headers().firstHeader("age") == null) "required" else "invalid_value",
                    message = CommonErrorCode.INVALID_HEADER_PARAMETER.getMessage(arrayOf("age")),
                ),
            )
        val name = request.queryParamOrNull("name")
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.INVALID_PARAMETER,
                fieldError = fieldError(
                    source = ErrorSource.QUERY,
                    field = "name",
                    reason = "required",
                    message = CommonErrorCode.INVALID_PARAMETER.getMessage(arrayOf("name")),
                ),
            )
        val gender = request.pathVariableOrNull("gender")
            ?.let { raw -> enumValues<Gender>().firstOrNull { it.name == raw } }
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.INVALID_PARAMETER,
                fieldError = fieldError(
                    source = ErrorSource.PATH,
                    field = "gender",
                    reason = "invalid_enum",
                    message = CommonErrorCode.INVALID_PARAMETER.getMessage(arrayOf("gender")),
                ),
            )
        val requestBody = request.awaitBodyOrNull<SampleRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.EMPTY_BODY,
                fieldError = fieldError(
                    source = ErrorSource.BODY,
                    field = "body",
                    reason = "required",
                    message = CommonErrorCode.EMPTY_BODY.getMessage(),
                ),
            )

        val response = SampleResponse.from(
            sampleUseCase.putSample(
                PutSampleCommand(
                    name = name,
                    age = age,
                    gender = gender,
                    id = requestBody.id,
                    ttl = requestBody.ttl,
                )
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    private fun fieldError(
        source: ErrorSource,
        field: String,
        reason: String,
        message: String,
    ): ApiFieldError {
        return ApiFieldError(
            source = source.wireName,
            field = field,
            reason = reason,
            message = message,
        )
    }
}
