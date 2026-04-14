package org.test.kotlin_base.adapter.input.web.hello

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.test.kotlin_base.application.port.input.hello.GetHelloUseCase

@Component
class HelloHandler(
    private val getHelloUseCase: GetHelloUseCase,
) {

    suspend fun getHello(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().bodyValueAndAwait(getHelloUseCase.getHello())
    }
}
