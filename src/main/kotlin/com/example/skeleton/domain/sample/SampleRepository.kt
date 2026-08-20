package com.example.skeleton.domain.sample

import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.domain.sample.model.SampleStatus

interface SampleRepository {
    fun findAll(): List<Sample>
    fun findByFilter(name: String?, minAge: Int?, maxAge: Int?, status: SampleStatus?): List<Sample>
    fun findByStatus(status: SampleStatus): List<Sample>
    fun findById(id: Long): Sample?
    fun insert(name: String, age: Int, status: SampleStatus): Sample
    fun update(id: Long, name: String, age: Int, status: SampleStatus): Boolean
    fun delete(id: Long): Boolean
}
