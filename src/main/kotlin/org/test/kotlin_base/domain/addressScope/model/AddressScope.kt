package org.test.kotlin_base.domain.addressScope.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("address_scopes")
data class AddressScope(
    @Id
    @Column("id")
    val id: String,
    @Column("vpc_id")
    val vpcId: String,
    @Column("status")
    val status: Int? = null,
    @Column("created_at")
    val created: LocalDateTime,
    @Column("updated_at")
    val updated: LocalDateTime,
)
