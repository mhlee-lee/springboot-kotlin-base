package com.example.skeleton.application.port.input.sample.model

import com.example.skeleton.domain.sample.model.Gender

data class PutSampleResult(val name: String, val age: Int, val gender: Gender, val id: String, val ttl: Int)
