package org.test.kotlin_base.presentation.hello

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.test.kotlin_base.common.TransactionExecutor
import org.test.kotlin_base.common.extensions.toJson
import org.test.kotlin_base.domain.addressScope.AddressScopeRepository
import org.test.kotlin_base.domain.sample.SampleRepository

@Component
class HelloHandler(
    private val sampleRepository: SampleRepository,
    private val addressScopeRepository: AddressScopeRepository,
    private val transactionExecutor: TransactionExecutor,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun getHello(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().bodyValueAndAwait("Hello")
    }
}
