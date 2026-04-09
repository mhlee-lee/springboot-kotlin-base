package org.test.kotlin_base.presentation.sample

import jakarta.validation.Validator
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.test.kotlin_base.common.TransactionExecutor
import org.test.kotlin_base.common.exception.EmptyBodyException
import org.test.kotlin_base.common.exception.InvalidHeaderParameterException
import org.test.kotlin_base.common.exception.InvalidRequestParameterException
import org.test.kotlin_base.common.extensions.toJson
import org.test.kotlin_base.common.extensions.validateOrThrow
import org.test.kotlin_base.domain.addressScope.AddressScopeRepository
import org.test.kotlin_base.domain.sample.SampleRepository
import org.test.kotlin_base.presentation.enums.Gender
import org.test.kotlin_base.presentation.sample.protocol.SampleRequest
import org.test.kotlin_base.presentation.sample.protocol.SampleResponse

@Component
class SampleHandler(
    private val sampleRepository: SampleRepository,
    private val addressScopeRepository: AddressScopeRepository,
    private val transactionExecutor: TransactionExecutor,
    private val validator: Validator,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun getSample(request: ServerRequest): ServerResponse {
        val value1 = transactionExecutor.execute {
            sampleRepository.findAll().toList()
        }
        val value2 = transactionExecutor.executeReadonly {
            sampleRepository.findAll().toList()
        }

        log.info("value1: ${value1.toJson()}")
        log.info("value2: ${value2.toJson()}")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun addressScope(request: ServerRequest): ServerResponse {
        val value1 = transactionExecutor.execute {
            addressScopeRepository.findAll().toList()
        }
        val value2 = transactionExecutor.executeReadonly {
            addressScopeRepository.findAll().toList()
        }

        log.info("value1: ${value1.toJson()}")
        log.info("value2: ${value2.toJson()}")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun putSample(request: ServerRequest): ServerResponse {
        val age = request.headers().firstHeader("age")?.toIntOrNull()
            ?: throw InvalidHeaderParameterException("age")
        val name = request.queryParamOrNull("name")
            ?: throw InvalidRequestParameterException("name")
        val gender = request.pathVariableOrNull("gender")
            ?.let { raw -> enumValues<Gender>().firstOrNull { it.name == raw } }
            ?: throw InvalidRequestParameterException("gender")
        val requestBody = request.awaitBodyOrNull<SampleRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw EmptyBodyException()

        val response = SampleResponse(name, age, gender, requestBody.id, requestBody.ttl)

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
