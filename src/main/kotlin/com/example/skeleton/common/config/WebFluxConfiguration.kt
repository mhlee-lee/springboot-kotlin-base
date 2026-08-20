package com.example.skeleton.common.config

import com.example.skeleton.common.constant.CommonConstant
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.webflux.error.ErrorAttributes
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.validation.DefaultMessageCodesResolver
import org.springframework.validation.MessageCodesResolver
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.ApiVersionConfigurer
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableConfigurationProperties(CorsProperties::class)
class WebFluxConfiguration(
    private val messageSource: MessageSource,
    private val corsProperties: CorsProperties,
) : WebFluxConfigurer {
    @Bean
    fun messageCodesResolver(): MessageCodesResolver = DefaultMessageCodesResolver().apply {
        setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE)
    }

    @Bean
    fun globalErrorAttributes(messageCodesResolver: MessageCodesResolver): ErrorAttributes =
        GlobalErrorAttributes(messageCodesResolver)

    @Bean
    fun applicationValidator(): LocalValidatorFactoryBean = LocalValidatorFactoryBean().apply {
        setValidationMessageSource(messageSource)
    }

    override fun getValidator(): Validator = applicationValidator()

    override fun getMessageCodesResolver(): MessageCodesResolver = messageCodesResolver()

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*corsProperties.allowedOrigins.toTypedArray())
            .allowedMethods(*corsProperties.allowedMethods.toTypedArray())
            .allowedHeaders(*corsProperties.allowedHeaders.toTypedArray())
            .allowCredentials(corsProperties.allowCredentials)
            .maxAge(corsProperties.maxAge)
    }

    override fun configureApiVersioning(configurer: ApiVersionConfigurer) {
        configurer
            .usePathSegment(1)
            .setVersionRequired(true)
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().maxInMemorySize(CommonConstant.MAX_BUFFER_SIZE)
    }
}
