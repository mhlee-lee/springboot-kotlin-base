package org.test.kotlin_base.presentation.sample.protocol

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

class SampleRequest(
    @field:NotBlank
    val id: String,
    @field:Positive
    val ttl: Int,
)
