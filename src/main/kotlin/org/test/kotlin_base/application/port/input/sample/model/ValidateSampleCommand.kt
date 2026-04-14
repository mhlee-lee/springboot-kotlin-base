package org.test.kotlin_base.application.port.input.sample.model

data class ValidateSampleCommand(val quantity: Int, val name: String, val requiredValue: String, val code: String)
