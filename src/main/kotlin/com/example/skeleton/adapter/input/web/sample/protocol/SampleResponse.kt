package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.domain.sample.model.SampleStatus

data class SampleResponse(val id: Long, val name: String, val age: Int, val status: SampleStatus) {
    companion object {
        fun from(sample: Sample) = SampleResponse(
            id = sample.id,
            name = sample.name,
            age = sample.age,
            status = sample.status,
        )
    }
}
