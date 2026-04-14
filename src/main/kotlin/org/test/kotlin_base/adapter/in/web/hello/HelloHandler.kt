package org.test.kotlin_base.adapter.`in`.web.hello

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.test.kotlin_base.application.port.`in`.hello.GetHelloUseCase

@Component
class HelloHandler(
    private val getHelloUseCase: GetHelloUseCase,
) {

    suspend fun getHello(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.IO) {
            ServerResponse.ok().bodyValueAndAwait(getHelloUseCase.getHello())
        }
    }
}
