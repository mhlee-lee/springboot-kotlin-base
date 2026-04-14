package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.ValidateSampleResult

data class SampleValidationResponse(val quantity: Int, val name: String, val requiredValue: String, val code: String) {
    companion object {
        fun from(result: ValidateSampleResult): SampleValidationResponse = SampleValidationResponse(
            quantity = result.quantity,
            name = result.name,
            requiredValue = result.requiredValue,
            code = result.code,
        )
    }
}
