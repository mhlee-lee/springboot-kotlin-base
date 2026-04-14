package org.test.kotlin_base.adapter.out.persistence.r2dbc.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.data.convert.WritingConverter
import org.test.kotlin_base.common.enums.GenericEnum

object GenericEnumConverterFactory {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 모든 GenericEnum → String 변환을 담당하는 단일 Writing Converter.
     * 새 GenericEnum enum이 추가되어도 이 Converter는 그대로 사용된다.
     */
    @WritingConverter
    object GenericEnumWritingConverter : Converter<GenericEnum, String> {
        override fun convert(source: GenericEnum): String = source.value
    }

    /**
     * String → 특정 GenericEnum 변환 Converter를 생성한다.
     *
     * Kotlin 람다를 Converter<String, E>로 반환하면 제네릭 타입이 소거되어
     * Spring Data가 변환 대상 타입을 결정하지 못한다.
     * GenericConverter를 사용해 getConvertibleTypes()에서 타입 쌍을 명시한다.
     */
    fun readingConverter(enumClass: Class<*>): GenericConverter {
        require(enumClass.isEnum) { "${enumClass.simpleName} is not an enum" }
        require(GenericEnum::class.java.isAssignableFrom(enumClass)) {
            "${enumClass.simpleName} does not implement GenericEnum"
        }

        return object : GenericConverter {
            override fun getConvertibleTypes(): Set<GenericConverter.ConvertiblePair> =
                setOf(GenericConverter.ConvertiblePair(String::class.java, enumClass))

            override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
                source ?: return null
                val value = source as String
                return enumClass.enumConstants
                    ?.firstOrNull { (it as GenericEnum).value == value }
                    ?: throw IllegalArgumentException(
                        "Unknown value '$value' for ${enumClass.simpleName}"
                    )
            }
        }
    }

    /**
     * 지정한 패키지 하위에서 GenericEnum을 구현한 enum을 스캔해
     * Reading Converter 목록을 자동으로 생성한다.
     *
     * 새 GenericEnum enum을 추가해도 R2dbcConfiguration 수정이 불필요하다.
     */
    fun scanReadingConverters(basePackage: String): List<GenericConverter> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(GenericEnum::class.java))

        return scanner.findCandidateComponents(basePackage).mapNotNull { beanDef ->
            runCatching {
                val clazz = Class.forName(beanDef.beanClassName)
                if (clazz.isEnum) readingConverter(clazz)
                    .also { log.debug("Registered GenericEnum converter: {}", clazz.simpleName) }
                else null
            }.getOrElse {
                log.warn("Failed to create converter for ${beanDef.beanClassName}", it)
                null
            }
        }
    }
}
