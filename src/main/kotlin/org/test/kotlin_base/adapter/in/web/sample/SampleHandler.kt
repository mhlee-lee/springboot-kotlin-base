package org.test.kotlin_base.adapter.`in`.web.sample

import jakarta.validation.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.test.kotlin_base.adapter.`in`.web.sample.protocol.*
import org.test.kotlin_base.application.port.`in`.sample.SampleUseCase
import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleCommand
import org.test.kotlin_base.common.exception.*
import org.test.kotlin_base.common.extensions.validateOrThrow
import org.test.kotlin_base.domain.sample.model.Gender

@Component
class SampleHandler(
    private val sampleUseCase: SampleUseCase,
    private val validator: Validator,
) {
    suspend fun getSample(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.IO) {
            sampleUseCase.getSamples()
            ServerResponse.ok().buildAndAwait()
        }
    }

    suspend fun addressScope(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.IO) {
            val result = sampleUseCase.getAddressScopes()
            val response = result.map { AddressScopeResponse.byDomain(it) }
            ServerResponse.ok().bodyValueAndAwait(response)
        }
    }

    suspend fun putSample(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.IO) {
            val rawAge = request.headers().firstHeader("age")
                ?: throw RequiredHeaderException("age")
            val age = rawAge.toIntOrNull()
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

            ServerResponse.ok().bodyValueAndAwait(response)
        }
    }

    suspend fun validateSample(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.IO) {
            val requestBody = request.awaitBodyOrNull<SampleValidationRequest>()
                ?.let(validator::validateOrThrow)
                ?: throw RequiredRequestBodyException()

            val response = SampleValidationResponse.from(
                sampleUseCase.validateSample(requestBody.toCommand())
            )

            ServerResponse.ok().bodyValueAndAwait(response)
        }
    }
}
