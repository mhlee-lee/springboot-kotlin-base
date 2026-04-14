package org.test.kotlin_base.adapter.out.persistence.jpa.config

import jakarta.persistence.AttributeConverter
import org.test.kotlin_base.common.enums.GenericEnum

abstract class GenericEnumConverter<T : GenericEnum>(
    private val enumClass: Class<T>
) : AttributeConverter<T, String> {

    override fun convertToDatabaseColumn(attribute: T?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): T? =
        dbData?.let { v -> enumClass.enumConstants?.firstOrNull { it.value == v } }
}
