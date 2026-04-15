package com.example.skeleton.application.port.input.sample.model

data class UpdateSampleCommand(val id: Long, val name: String, val age: Int, val modifiedBy: String)
