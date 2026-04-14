package com.example.skeleton.application.port.input.hello

interface GetHelloUseCase {
    suspend fun getHello(): String
}
