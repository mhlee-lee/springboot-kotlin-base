package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.application.port.input.sample.model.PutSampleResult
import com.example.skeleton.domain.sample.model.Gender

data class SampleResponse(val name: String, val age: Int, val gender: Gender, val id: String, val ttl: Int) {
    companion object {
        fun from(result: PutSampleResult): SampleResponse = SampleResponse(
            name = result.name,
            age = result.age,
            gender = result.gender,
            id = result.id,
            ttl = result.ttl,
        )
    }
}
