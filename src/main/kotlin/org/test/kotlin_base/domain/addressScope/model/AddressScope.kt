package org.test.kotlin_base.domain.addressscope.model

import java.time.LocalDateTime

data class AddressScope(
    val id: String,
    val vpcId: String,
    val status: Int? = null,
    val created: LocalDateTime,
    val updated: LocalDateTime,
)
