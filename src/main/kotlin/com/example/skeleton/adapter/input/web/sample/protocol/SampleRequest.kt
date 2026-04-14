package com.example.skeleton.adapter.input.web.sample.protocol

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class SampleRequest(
    @field:NotBlank
    val id: String,
    @field:Positive
    val ttl: Int,
)
