package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.CreateSampleCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CreateSampleRequest(
    @field:NotBlank
    val name: String,
    @field:Min(0)
    @field:Max(200)
    val age: Int,
) {
    fun toCommand() = CreateSampleCommand(name = name, age = age)
}
