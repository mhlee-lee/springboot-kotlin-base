package com.example.skeleton.adapter.input.web.hello

import com.example.skeleton.common.constant.CommonConstant.API_VERSION_V1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.version
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class HelloRouter(private val helloHandler: HelloHandler) {
    @Bean
    fun coRouteHello(): RouterFunction<ServerResponse> = coRouter {
        (accept(MediaType.APPLICATION_JSON) and version(API_VERSION_V1) and "/hello/{version}").nest {
            GET("hello", helloHandler::getHello)
        }
    }
}
