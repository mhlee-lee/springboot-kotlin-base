package org.test.kotlin_base.adapter.out.persistence.jpa.addressscope.entity

import jakarta.persistence.*
import org.test.kotlin_base.adapter.out.persistence.jpa.addressscope.AddressTypeConverter
import org.test.kotlin_base.domain.addressscope.model.AddressScope
import org.test.kotlin_base.domain.addressscope.model.AddressType
import java.time.LocalDateTime

@Entity
@Table(name = "address_scopes")
class AddressScopeEntity(
    @Id
    @Column(name = "id")
    var id: String = "",
    @Column(name = "vpc_id")
    var vpcId: String = "",
    @Column(name = "status")
    var status: Int? = null,
    @Convert(converter = AddressTypeConverter::class)
    @Column(name = "address_type")
    var addressType: AddressType = AddressType.RESIDENTIAL,
    @Column(name = "created_at")
    var created: LocalDateTime = LocalDateTime.MIN,
    @Column(name = "updated_at")
    var updated: LocalDateTime = LocalDateTime.MIN,
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
