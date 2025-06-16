package org.test.kotlin_base.domain.addressScope.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "address_scopes")
class AddressScope(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "vpc_id", nullable = false)
    var vpcId: String? = null,

    @Column(nullable = true)
    var status: Integer? = null,

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    var created: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    var updated: LocalDateTime? = null,
)
