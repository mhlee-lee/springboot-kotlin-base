package org.test.kotlin_base.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.test.kotlin_base.common.objectMapper
import tools.jackson.databind.json.JsonMapper

@Configuration
class CommonConfiguration {
    @Bean
    @Primary
    fun applicationObjectMapper(): JsonMapper = objectMapper
}
