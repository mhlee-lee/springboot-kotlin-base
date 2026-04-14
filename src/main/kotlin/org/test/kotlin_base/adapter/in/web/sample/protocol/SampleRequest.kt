package org.test.kotlin_base.adapter.`in`.web.sample.protocol

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class SampleRequest(
    @field:NotBlank
    val id: String,
    @field:Positive
    val ttl: Int,
)
