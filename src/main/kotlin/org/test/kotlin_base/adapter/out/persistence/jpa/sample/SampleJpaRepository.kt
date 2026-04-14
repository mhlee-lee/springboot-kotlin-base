package org.test.kotlin_base.adapter.out.persistence.jpa.sample

import org.springframework.data.jpa.repository.JpaRepository
import org.test.kotlin_base.adapter.out.persistence.jpa.sample.entity.SampleEntity

interface SampleJpaRepository : JpaRepository<SampleEntity, Long>
