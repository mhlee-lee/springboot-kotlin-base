package org.test.kotlin_base.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.test.kotlin_base.common.enums.DatePatternEnum
import java.time.LocalDate
import java.time.LocalDateTime

val objectMapper: ObjectMapper =
    ObjectMapper().registerModules(
        KotlinModule.Builder()
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, true)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build(), getJavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)


private fun getJavaTimeModule() = JavaTimeModule().apply {
    addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DatePatternEnum.DATETIME_DEFAULT.formatter))
    addSerializer(LocalDate::class.java, LocalDateSerializer(DatePatternEnum.DATE_DEFAULT.formatter))
    addDeserializer(
        LocalDateTime::class.java,
        LocalDateTimeDeserializer(DatePatternEnum.DATETIME_DEFAULT.formatter)
    )
    addDeserializer(
        LocalDate::class.java,
        LocalDateDeserializer(DatePatternEnum.DATE_DEFAULT.formatter)
    )
}
