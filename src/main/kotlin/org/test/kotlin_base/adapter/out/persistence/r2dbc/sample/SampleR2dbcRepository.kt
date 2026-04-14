package org.test.kotlin_base.adapter.out.persistence.r2dbc.sample

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.test.kotlin_base.adapter.out.persistence.r2dbc.sample.entity.SampleEntity

interface SampleR2dbcRepository : CoroutineCrudRepository<SampleEntity, Long>
