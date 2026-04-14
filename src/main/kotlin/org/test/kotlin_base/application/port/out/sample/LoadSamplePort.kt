package org.test.kotlin_base.application.port.out.sample

import org.test.kotlin_base.domain.sample.model.Sample

interface LoadSamplePort {
    suspend fun findAll(): List<Sample>
}
