package com.example.skeleton.domain.addressscope.model

import com.example.skeleton.common.enums.DisplayEnum
import com.example.skeleton.common.enums.GenericEnum

enum class AddressType(
    override val value: String,
    override val label: String,
    override val priority: Int,
    override val displayable: Boolean,
) : GenericEnum,
    DisplayEnum {
    RESIDENTIAL("ATEN0001", "enum.AddressType.RESIDENTIAL", 1, true),
    COMMERCIAL("ATEN0002", "enum.AddressType.COMMERCIAL", 2, true),
    ;

    companion object {
        fun fromValue(value: String): AddressType = entries.firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown AddressType value: $value")
    }
}
