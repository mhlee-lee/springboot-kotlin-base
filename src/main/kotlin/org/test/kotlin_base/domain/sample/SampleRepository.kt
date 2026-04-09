package org.test.kotlin_base.domain.sample

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.test.kotlin_base.domain.sample.model.SampleEntity

interface SampleRepository : CoroutineCrudRepository<SampleEntity, Long>
