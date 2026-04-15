package com.example.skeleton.application.port.input.sample

import com.example.skeleton.application.port.input.sample.model.CreateSampleCommand
import com.example.skeleton.application.port.input.sample.model.SampleSearchQuery
import com.example.skeleton.application.port.input.sample.model.UpdateSampleCommand
import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.domain.sample.model.SampleStatus

interface SampleUseCase {
    suspend fun searchSamples(query: SampleSearchQuery): List<Sample>
    suspend fun searchSamplesByStatus(status: SampleStatus): List<Sample>
    suspend fun getSample(id: Long): Sample
    suspend fun createSample(command: CreateSampleCommand): Sample
    suspend fun updateSample(command: UpdateSampleCommand): Sample
    suspend fun deleteSample(id: Long)
}
