package org.test.kotlin_base.common.config

import org.springframework.boot.http.client.HttpClientSettings
import org.springframework.boot.webclient.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class WebClientConfiguration {
    @Bean
    fun httpClientSettings(): HttpClientSettings {
        val timeout = Duration.ofSeconds(10)
        return HttpClientSettings.defaults().withTimeouts(timeout, timeout)
    }

    @Bean
    fun webClientCustomizer(): WebClientCustomizer = WebClientCustomizer { builder ->
        builder.codecs { configurer ->
            configurer.defaultCodecs().maxInMemorySize(-1)
        }
    }

    @Bean
    fun webClient(webClientBuilder: WebClient.Builder): WebClient = webClientBuilder.build()
}
