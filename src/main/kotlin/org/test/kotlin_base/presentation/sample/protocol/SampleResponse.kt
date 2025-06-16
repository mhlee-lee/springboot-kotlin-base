package org.test.kotlin_base.presentation.sample.protocol

import org.test.kotlin_base.presentation.enums.Gender

class SampleResponse(
    val name: String,
    val age: Int,
    val gender: Gender,
    val id: String,
    val ttl: Int,
)
