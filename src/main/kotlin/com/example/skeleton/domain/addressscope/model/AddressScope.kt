package com.example.skeleton.domain.addressscope.model

import java.time.LocalDateTime

data class AddressScope(
    val id: String,
    val vpcId: String,
    val status: Int? = null,
    val addressType: AddressType,
    val created: LocalDateTime,
    val updated: LocalDateTime,
)
