package org.test.kotlin_base.application.port.out.sample

import org.test.kotlin_base.domain.sample.model.Sample

interface LoadSamplePort {
    fun findAll(): List<Sample>
}
