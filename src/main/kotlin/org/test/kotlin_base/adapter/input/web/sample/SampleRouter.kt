package org.test.kotlin_base.adapter.input.web.sample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.version
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.test.kotlin_base.common.constant.CommonConstant.API_VERSION_V1

@Configuration
class SampleRouter(private val sampleHandler: SampleHandler) {
    @Bean
    fun coRouteSample(): RouterFunction<ServerResponse> = coRouter {
        (accept(MediaType.APPLICATION_JSON) and version(API_VERSION_V1) and "/sample/{version}").nest {
            GET("sample", sampleHandler::getSample)
            GET("addressScope", sampleHandler::addressScope)
            PUT("sample/{gender}", sampleHandler::putSample)
            POST("validation", sampleHandler::validateSample)
        }
    }
}
