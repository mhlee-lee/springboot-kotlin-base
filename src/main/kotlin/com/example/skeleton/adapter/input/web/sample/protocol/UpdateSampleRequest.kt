package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.UpdateSampleCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class UpdateSampleRequest(
    @field:NotBlank
    val name: String,
    @field:Min(0)
    @field:Max(200)
    val age: Int,
) {
    fun toCommand(id: Long, modifiedBy: String) = UpdateSampleCommand(
        id = id,
        name = name,
        age = age,
        modifiedBy = modifiedBy,
    )
}
