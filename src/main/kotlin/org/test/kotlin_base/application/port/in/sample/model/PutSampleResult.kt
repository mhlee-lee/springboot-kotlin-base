package org.test.kotlin_base.application.port.`in`.sample.model

import org.test.kotlin_base.domain.sample.model.Gender

data class PutSampleResult(
    val name: String,
    val age: Int,
    val gender: Gender,
    val id: String,
    val ttl: Int,
)
