package com.example.skeleton.adapter.input.web.sample

import com.example.skeleton.adapter.input.web.sample.protocol.*
import com.example.skeleton.application.port.input.sample.SampleUseCase
import com.example.skeleton.application.port.input.sample.model.PutSampleCommand
import com.example.skeleton.common.exception.InvalidEnumPathParameterException
import com.example.skeleton.common.exception.InvalidHeaderValueException
import com.example.skeleton.common.exception.RequiredQueryParameterException
import com.example.skeleton.common.exception.RequiredRequestBodyException
import com.example.skeleton.common.extensions.headerOrThrow
import com.example.skeleton.common.extensions.validateOrThrow
import com.example.skeleton.domain.sample.model.Gender
import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class SampleHandler(private val sampleUseCase: SampleUseCase, private val validator: Validator) {
    suspend fun getSample(ignoredRequest: ServerRequest): ServerResponse {
        val result = sampleUseCase.getSamples()
        return ServerResponse.ok().bodyValueAndAwait(result)
    }

    suspend fun addressScope(ignoredRequest: ServerRequest): ServerResponse {
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
                ),
            ),
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun validateSample(request: ServerRequest): ServerResponse {
        val requestBody = request.awaitBodyOrNull<SampleValidationRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw RequiredRequestBodyException()

        val response = SampleValidationResponse.from(
            sampleUseCase.validateSample(requestBody.toCommand()),
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
