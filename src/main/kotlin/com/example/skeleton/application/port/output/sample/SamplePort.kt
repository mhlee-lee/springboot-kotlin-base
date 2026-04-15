package com.example.skeleton.application.port.output.sample

import com.example.skeleton.domain.sample.model.Sample

interface SamplePort {
    fun findAll(): List<Sample>
    fun findByFilter(name: String?, minAge: Int?, maxAge: Int?): List<Sample>
    fun findById(id: Long): Sample?
    fun insert(name: String, age: Int): Sample
    fun update(id: Long, name: String, age: Int): Boolean
    fun delete(id: Long): Boolean
}
