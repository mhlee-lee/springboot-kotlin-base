package org.test.kotlin_base.common.config

import org.springframework.boot.webflux.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.JacksonJsonDecoder
import org.springframework.http.codec.json.JacksonJsonEncoder
import org.springframework.validation.DefaultMessageCodesResolver
import org.springframework.validation.MessageCodesResolver
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.test.kotlin_base.common.constant.CommonConstant
import org.test.kotlin_base.common.objectMapper
import org.test.kotlin_base.common.utils.MessageConverter
import tools.jackson.databind.SerializationFeature

@Configuration
@EnableWebFlux
class WebFluxConfiguration : WebFluxConfigurer {
    @Bean
    fun globalErrorAttributes() : ErrorAttributes {
        return GlobalErrorAttributes(messageResolver)
    }

    // 커스텀 Validator
    @Bean
    fun validator(): Validator {
        val bean = LocalValidatorFactoryBean()
        bean.setValidationMessageSource(MessageConverter.message)
        return bean
    }

    override fun getMessageCodesResolver(): MessageCodesResolver {
        return messageResolver
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().apply {
            jacksonJsonEncoder(JacksonJsonEncoder(configurationMapper, MediaType.APPLICATION_JSON))
            jacksonJsonDecoder(JacksonJsonDecoder(configurationMapper, MediaType.APPLICATION_JSON))
            maxInMemorySize(CommonConstant.MAX_BUFFER_SIZE)
        }
    }

    companion object {
        private val configurationMapper = objectMapper.rebuild().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).build()

        private val messageResolver = DefaultMessageCodesResolver().apply {
            // message code 저의 형식
            // POSTFIX_ERROR_CODE = [객체명].[필드명].[에러코드]
            // message properties 정의 : user.name.NotBlank
            setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE)
        }
    }
}
