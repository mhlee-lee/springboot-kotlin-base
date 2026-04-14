package com.example.skeleton.application.port.output.sample

import com.example.skeleton.domain.sample.model.Sample

interface LoadSamplePort {
    fun findAll(): List<Sample>
}
