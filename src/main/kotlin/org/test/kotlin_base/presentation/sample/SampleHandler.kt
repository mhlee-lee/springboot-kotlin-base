package org.test.kotlin_base.presentation.sample

import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.test.kotlin_base.common.TransactionExecutor
import org.test.kotlin_base.common.extensions.toJson
import org.test.kotlin_base.domain.addressScope.AddressScopeRepository
import org.test.kotlin_base.domain.sample.SampleRepository
import org.test.kotlin_base.presentation.enums.Gender
import org.test.kotlin_base.presentation.sample.protocol.SampleRequest
import org.test.kotlin_base.presentation.sample.protocol.SampleResponse
import java.security.InvalidParameterException

@Component
class SampleHandler(
    private val sampleRepository: SampleRepository,
    private val addressScopeRepository: AddressScopeRepository,
    private val transactionExecutor: TransactionExecutor,
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
        val age = request.headers().firstHeader("age")?.toIntOrNull() ?: throw InvalidParameterException("age")
        val name = request.queryParamOrNull("name") ?: throw InvalidParameterException("name")
        val gender: Gender = Gender.valueOf(request.pathVariableOrNull("gender") ?: throw InvalidParameterException("gender"))
        val requestBody = request.awaitBodyOrNull<SampleRequest>() ?: throw RuntimeException("body error")

        val response = SampleResponse(name, age, gender, requestBody.id, requestBody.ttl)

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
