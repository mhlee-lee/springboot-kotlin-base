package org.test.kotlin_base.application.port.input.hello

interface GetHelloUseCase {
    suspend fun getHello(): String
}
