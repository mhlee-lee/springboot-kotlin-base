package org.test.kotlin_base.common.config

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
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.test.kotlin_base.common.constant.CommonConstant

@Configuration
class WebFluxConfiguration(
    private val messageSource: MessageSource,
) : WebFluxConfigurer {
    @Bean
    fun messageCodesResolver(): MessageCodesResolver {
        return DefaultMessageCodesResolver().apply {
            setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE)
        }
    }

    @Bean
    fun globalErrorAttributes(messageCodesResolver: MessageCodesResolver): ErrorAttributes {
        return GlobalErrorAttributes(messageCodesResolver)
    }

    @Bean
    fun applicationValidator(): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean().apply {
            setValidationMessageSource(messageSource)
        }
    }

    override fun getValidator(): Validator {
        return applicationValidator()
    }

    override fun getMessageCodesResolver(): MessageCodesResolver {
        return messageCodesResolver()
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
