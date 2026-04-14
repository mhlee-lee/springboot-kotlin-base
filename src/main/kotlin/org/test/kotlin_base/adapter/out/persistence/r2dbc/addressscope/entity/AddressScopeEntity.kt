package org.test.kotlin_base.adapter.out.persistence.r2dbc.addressscope.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.test.kotlin_base.domain.addressscope.model.AddressScope
import org.test.kotlin_base.domain.addressscope.model.AddressType
import java.time.LocalDateTime

@Table("address_scopes")
data class AddressScopeEntity(
    @Id
    @Column("id")
    val id: String,
    @Column("vpc_id")
    val vpcId: String,
    @Column("status")
    val status: Int? = null,
    @Column("address_type")
    val addressType: AddressType,
    @Column("created_at")
    val created: LocalDateTime,
    @Column("updated_at")
    val updated: LocalDateTime,
)

fun AddressScopeEntity.toDomain(): AddressScope {
    return AddressScope(
        id = id,
        vpcId = vpcId,
        status = status,
        addressType = addressType,
        created = created,
        updated = updated,
    )
}
