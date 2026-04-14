package org.test.kotlin_base.application.port.`in`.hello

interface GetHelloUseCase {
    suspend fun getHello(): String
}
