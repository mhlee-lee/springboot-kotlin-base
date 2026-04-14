package org.test.kotlin_base.adapter.input.web.sample

import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.test.kotlin_base.adapter.input.web.sample.protocol.*
import org.test.kotlin_base.application.port.input.sample.SampleUseCase
import org.test.kotlin_base.application.port.input.sample.model.PutSampleCommand
import org.test.kotlin_base.common.exception.InvalidEnumPathParameterException
import org.test.kotlin_base.common.exception.InvalidHeaderValueException
import org.test.kotlin_base.common.exception.RequiredQueryParameterException
import org.test.kotlin_base.common.exception.RequiredRequestBodyException
import org.test.kotlin_base.common.extensions.headerOrThrow
import org.test.kotlin_base.common.extensions.validateOrThrow
import org.test.kotlin_base.domain.sample.model.Gender

@Component
class SampleHandler(
    private val sampleUseCase: SampleUseCase,
    private val validator: Validator,
) {
    suspend fun getSample(request: ServerRequest): ServerResponse {
        val result = sampleUseCase.getSamples()
        return ServerResponse.ok().bodyValueAndAwait(result)
    }

    suspend fun addressScope(request: ServerRequest): ServerResponse {
        val result = sampleUseCase.getAddressScopes()
        val response = result.map { AddressScopeResponse.byDomain(it) }
        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun putSample(request: ServerRequest): ServerResponse {
        val age = request.headerOrThrow("age").toIntOrNull()
            ?: throw InvalidHeaderValueException("age")
        val name = request.queryParamOrNull("name")
            ?: throw RequiredQueryParameterException("name")
        val gender = request.pathVariableOrNull("gender")
            ?.let { raw -> runCatching { enumValueOf<Gender>(raw) }.getOrNull() }
            ?: throw InvalidEnumPathParameterException("gender")
        val requestBody = request.awaitBodyOrNull<SampleRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw RequiredRequestBodyException()

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

    suspend fun validateSample(request: ServerRequest): ServerResponse {
        val requestBody = request.awaitBodyOrNull<SampleValidationRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw RequiredRequestBodyException()

        val response = SampleValidationResponse.from(
            sampleUseCase.validateSample(requestBody.toCommand())
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
