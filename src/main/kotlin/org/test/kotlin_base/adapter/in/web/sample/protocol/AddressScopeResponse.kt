package org.test.kotlin_base.adapter.`in`.web.sample.protocol

import org.test.kotlin_base.domain.addressscope.model.AddressScope
import java.time.LocalDateTime

/**
 * @author MooHee Lee
 */
class AddressScopeResponse(
    val id: String,
    val vpcId: String,
    val status: Int? = null,
    val addressType: AddressType,
    val created: LocalDateTime,
    val updated: LocalDateTime,
) {
    companion object {
        fun byDomain(addressScope: AddressScope): AddressScopeResponse {
            return AddressScopeResponse(
                id = addressScope.id,
                vpcId = addressScope.vpcId,
                status = addressScope.status,
                addressType = AddressType.byDomain(addressScope.addressType),
                created = addressScope.created,
                updated = addressScope.updated,
            )
        }
    }
}
