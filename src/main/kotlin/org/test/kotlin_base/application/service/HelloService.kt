package org.test.kotlin_base.application.service

import org.springframework.stereotype.Service
import org.test.kotlin_base.application.port.input.hello.GetHelloUseCase

@Service
class HelloService : GetHelloUseCase {
    override suspend fun getHello(): String {
        return "Hello"
    }
}
