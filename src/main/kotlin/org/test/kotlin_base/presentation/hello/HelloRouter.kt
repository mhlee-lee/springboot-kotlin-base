package org.test.kotlin_base.presentation.hello

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class HelloRouter(private val helloHandler: HelloHandler) {
    @Bean
    fun coRouteHello(): RouterFunction<ServerResponse> {
        return coRouter {
            (accept(MediaType.APPLICATION_JSON) and "/hello/v1.0").nest {
                GET("hello", helloHandler::getHello)
            }
        }
    }
}
