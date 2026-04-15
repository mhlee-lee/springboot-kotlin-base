package com.example.skeleton.application.port.input.sample.model

import com.example.skeleton.domain.sample.model.SampleStatus

data class SampleSearchQuery(val name: String?, val minAge: Int?, val maxAge: Int?, val status: SampleStatus?)
