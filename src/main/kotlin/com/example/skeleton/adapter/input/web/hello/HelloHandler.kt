package com.example.skeleton.adapter.input.web.hello

import com.example.skeleton.application.port.input.hello.GetHelloUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class HelloHandler(private val getHelloUseCase: GetHelloUseCase) {
    suspend fun getHello(ignoredRequest: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyValueAndAwait(getHelloUseCase.getHello())
}
