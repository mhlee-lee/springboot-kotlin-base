package com.example.skeleton.adapter.input.web.sample.protocol

import com.example.skeleton.domain.sample.model.Sample

data class SampleResponse(val id: Long, val name: String, val age: Int) {
    companion object {
        fun from(sample: Sample) = SampleResponse(id = sample.id, name = sample.name, age = sample.age)
    }
}
