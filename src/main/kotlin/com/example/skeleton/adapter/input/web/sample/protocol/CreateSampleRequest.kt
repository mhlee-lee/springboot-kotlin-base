package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.CreateSampleCommand
import com.example.skeleton.domain.sample.model.SampleStatus
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateSampleRequest(
    @field:NotBlank
    val name: String,
    @field:Min(0)
    @field:Max(200)
    val age: Int,
    // Body에서 enum 바인딩 예시: JSON { "status": "ACTIVE" }
    @field:NotNull
    val status: SampleStatus,
) {
    fun toCommand() = CreateSampleCommand(name = name, age = age, status = status)
}
