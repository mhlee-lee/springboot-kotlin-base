package org.test.kotlin_base.presentation.sample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class SampleRouter(private val sampleHandler: SampleHandler) {
    @Bean
    fun coRouteSample(): RouterFunction<ServerResponse> {
        return coRouter {
            (accept(MediaType.APPLICATION_JSON) and "/sample/v1.0").nest {
                GET("sample", sampleHandler::getSample)
                GET("addressScope", sampleHandler::addressScope)
                PUT("sample", sampleHandler::putSample)
            }
        }
    }
}
