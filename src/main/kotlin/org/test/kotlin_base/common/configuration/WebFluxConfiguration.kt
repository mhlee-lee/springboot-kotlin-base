package org.test.kotlin_base.common.configuration

import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.validation.DefaultMessageCodesResolver
import org.springframework.validation.MessageCodesResolver
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.test.kotlin_base.common.constant.CommonConstant
import org.test.kotlin_base.common.objectMapper
import org.test.kotlin_base.common.utils.MessageConverter

@Configuration
@EnableWebFlux
class WebFluxConfiguration : WebFluxConfigurer {

    override fun getMessageCodesResolver(): MessageCodesResolver? {
        return messageResolver
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().apply {
            jackson2JsonEncoder(jackson2JsonEncoder)
            jackson2JsonDecoder(jackson2JsonDecoder)
            maxInMemorySize(CommonConstant.MAX_BUFFER_SIZE)
        }
    }

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

    companion object {
        private val configurationMapper = objectMapper.copy().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        private val jackson2JsonEncoder = Jackson2JsonEncoder(configurationMapper)
        private val jackson2JsonDecoder = Jackson2JsonDecoder(configurationMapper)

        private val messageResolver = DefaultMessageCodesResolver().apply {
            // message code 저의 형식
            // POSTFIX_ERROR_CODE = [객체명].[필드명].[에러코드]
            // message properties 정의 : user.name.NotBlank
            setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE)
        }
    }
}
