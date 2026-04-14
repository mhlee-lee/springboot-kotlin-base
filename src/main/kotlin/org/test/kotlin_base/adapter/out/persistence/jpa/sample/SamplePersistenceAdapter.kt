package org.test.kotlin_base.adapter.out.persistence.jpa.sample

import org.springframework.stereotype.Component
import org.test.kotlin_base.adapter.out.persistence.jpa.sample.entity.toDomain
import org.test.kotlin_base.application.port.out.sample.LoadSamplePort
import org.test.kotlin_base.domain.sample.model.Sample

@Component
class SamplePersistenceAdapter(
    private val sampleJpaRepository: SampleJpaRepository,
) : LoadSamplePort {
    override fun findAll(): List<Sample> {
        return sampleJpaRepository.findAll()
            .map { it.toDomain() }
    }
}
