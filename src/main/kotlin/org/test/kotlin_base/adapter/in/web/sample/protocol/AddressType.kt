package org.test.kotlin_base.adapter.`in`.web.sample.protocol

import org.test.kotlin_base.common.enums.DisplayEnum

/**
 * @author MooHee Lee
 */
enum class AddressType(
    override val label: String,
    override val priority: Int,
    override val displayable: Boolean,
) : DisplayEnum {
    RESIDENTIAL("enum.AddressType.RESIDENTIAL", 1, true),
    COMMERCIAL("enum.AddressType.COMMERCIAL", 1, true),
    ;

    companion object {
        fun byDomain(domain: org.test.kotlin_base.domain.addressscope.model.AddressType) = when (domain) {
            org.test.kotlin_base.domain.addressscope.model.AddressType.RESIDENTIAL -> RESIDENTIAL
            org.test.kotlin_base.domain.addressscope.model.AddressType.COMMERCIAL -> COMMERCIAL
        }
    }
}
