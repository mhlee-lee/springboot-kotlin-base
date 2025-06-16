package org.test.kotlin_base.presentation.sample

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.pathVariableOrNull
import org.springframework.web.reactive.function.server.queryParamOrNull
import org.test.kotlin_base.common.TransactionExecutor
import org.test.kotlin_base.common.extensions.header
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
            sampleRepository.findAll()
        }
        val value2 = transactionExecutor.executeReadonly {
            sampleRepository.findAll()
        }

        log.info("value1: ${value1.toJson()}")
        log.info("value2: ${value2.toJson()}")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun addressScope(request: ServerRequest): ServerResponse {
        val value1 = transactionExecutor.execute {
            addressScopeRepository.findAll()
        }
        val value2 = transactionExecutor.executeReadonly {
            addressScopeRepository.findAll()
        }

        log.info("value1: ${value1.toJson()}")
        log.info("value2: ${value2.toJson()}")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun putSample(request: ServerRequest): ServerResponse {
        val age = request.header("age")?.toIntOrNull() ?: throw InvalidParameterException("age")
        val name = request.queryParamOrNull("name") ?: throw InvalidParameterException("name")
        val gender: Gender = Gender.valueOf(request.pathVariableOrNull("gender") ?: throw InvalidParameterException("gender"))
        val requestBody = request.awaitBodyOrNull<SampleRequest>() ?: throw RuntimeException("body error")

        val response = SampleResponse(name, age, gender, requestBody.id, requestBody.ttl)

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
