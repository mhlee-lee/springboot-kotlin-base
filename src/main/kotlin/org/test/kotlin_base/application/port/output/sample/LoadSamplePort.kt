package org.test.kotlin_base.application.port.output.sample

import org.test.kotlin_base.domain.sample.model.Sample

interface LoadSamplePort {
    fun findAll(): List<Sample>
}
