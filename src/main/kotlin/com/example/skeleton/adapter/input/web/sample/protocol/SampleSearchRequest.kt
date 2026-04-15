package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.SampleSearchQuery
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SampleSearchRequest(
    val name: String? = null,
    @field:Min(0)
    val minAge: Int? = null,
    @field:Max(200)
    val maxAge: Int? = null,
) {
    fun toQuery() = SampleSearchQuery(name = name, minAge = minAge, maxAge = maxAge)
}
