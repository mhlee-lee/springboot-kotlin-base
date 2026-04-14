package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.ValidateSampleCommand
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range

data class SampleValidationRequest(
    @field:NotNull
    @field:Range(min = 1, max = 2000, message = "{validation.constraints.Range}")
    val quantity: Int?,
    @field:NotNull
    @field:Size(min = 2, max = 10, message = "{validation.constraints.Size}")
    val name: String?,
    @field:NotNull
    val requiredValue: String?,
    @field:NotNull
    @field:Pattern(regexp = "^[A-Z]{3}-\\d{2}$")
    val code: String?,
    @field:NotNull(message = "{SampleValidationRequest.validation.ttl}")
    val ttl: Int? = null,
) {
    fun toCommand(): ValidateSampleCommand = ValidateSampleCommand(
        quantity = requireNotNull(quantity),
        name = requireNotNull(name),
        requiredValue = requireNotNull(requiredValue),
        code = requireNotNull(code),
    )
}
