package org.test.kotlin_base.adapter.input.web.sample.protocol

import org.test.kotlin_base.application.port.input.sample.model.ValidateSampleResult

data class SampleValidationResponse(
    val quantity: Int,
    val name: String,
    val requiredValue: String,
    val code: String,
) {
    companion object {
        fun from(result: ValidateSampleResult): SampleValidationResponse {
            return SampleValidationResponse(
                quantity = result.quantity,
                name = result.name,
                requiredValue = result.requiredValue,
                code = result.code,
            )
        }
    }
}
