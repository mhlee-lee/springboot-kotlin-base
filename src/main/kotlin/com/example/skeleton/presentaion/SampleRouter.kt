package com.example.skeleton.presentaion

import com.example.skeleton.common.constant.CommonConstant.API_VERSION_V1
import com.example.skeleton.common.extensions.coRouterWithMdc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.version
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class SampleRouter(private val handler: SampleHandler) {
    @Bean
    fun sampleRoutes(): RouterFunction<ServerResponse> = coRouterWithMdc {
        (accept(MediaType.APPLICATION_JSON) and version(API_VERSION_V1) and "/sample/{version}").nest {
            GET("samples", handler::searchSamples)
            GET("samples/status/{status}", handler::searchSamplesByStatus) // Path variable enum 예시
            GET("samples/{id}", handler::getSample)
            POST("samples", handler::createSample)
            PUT("samples/{id}", handler::updateSample)
            DELETE("samples/{id}", handler::deleteSample)
        }
    }
}
