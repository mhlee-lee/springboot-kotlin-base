package org.test.kotlin_base.adapter.out.persistence.r2dbc.sample

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.test.kotlin_base.adapter.out.persistence.r2dbc.sample.entity.toDomain
import org.test.kotlin_base.application.port.out.sample.LoadSamplePort
import org.test.kotlin_base.domain.sample.model.Sample

@Component
class SamplePersistenceAdapter(
    private val sampleR2dbcRepository: SampleR2dbcRepository,
) : LoadSamplePort {
    override suspend fun findAll(): List<Sample> {
        return sampleR2dbcRepository.findAll()
            .toList()
            .map { it.toDomain() }
    }
}
