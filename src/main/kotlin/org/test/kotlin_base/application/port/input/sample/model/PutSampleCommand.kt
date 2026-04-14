package org.test.kotlin_base.application.port.input.sample.model

import org.test.kotlin_base.domain.sample.model.Gender

data class PutSampleCommand(val name: String, val age: Int, val gender: Gender, val id: String, val ttl: Int)
