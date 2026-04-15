package com.example.skeleton.application.port.input.sample.model

import com.example.skeleton.domain.sample.model.SampleStatus

data class CreateSampleCommand(val name: String, val age: Int, val status: SampleStatus)
