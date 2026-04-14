package org.test.kotlin_base.adapter.out.persistence.r2dbc.addressscope.entity

import org.test.kotlin_base.common.enums.GenericEnum

/**
 * @author MooHee Lee
 */
enum class AddressType(
    override val value: String,
) : GenericEnum {
    RESIDENTIAL("ATEN0001"),
    COMMERCIAL("ATEN0002"),
    ;

    fun toDomainEnum() = when (this) {
        RESIDENTIAL -> org.test.kotlin_base.domain.addressscope.model.AddressType.RESIDENTIAL
        COMMERCIAL -> org.test.kotlin_base.domain.addressscope.model.AddressType.COMMERCIAL
    }
}
