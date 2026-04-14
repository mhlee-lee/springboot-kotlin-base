package com.example.skeleton.application.service

import com.example.skeleton.application.port.input.hello.GetHelloUseCase
import org.springframework.stereotype.Service

@Service
class HelloService : GetHelloUseCase {
    override suspend fun getHello(): String = "Hello"
}
