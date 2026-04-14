package org.test.kotlin_base.adapter.`in`.web.sample.protocol

import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleResult
import org.test.kotlin_base.domain.sample.model.Gender

data class SampleResponse(
    val name: String,
    val age: Int,
    val gender: Gender,
    val id: String,
    val ttl: Int,
) {
    companion object {
        fun from(result: PutSampleResult): SampleResponse {
            return SampleResponse(
                name = result.name,
                age = result.age,
                gender = result.gender,
                id = result.id,
                ttl = result.ttl,
            )
        }
    }
}
