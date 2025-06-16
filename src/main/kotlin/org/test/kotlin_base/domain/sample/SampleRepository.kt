package org.test.kotlin_base.domain.sample

import org.springframework.data.jpa.repository.JpaRepository
import org.test.kotlin_base.infrastructure.database.repository.SampleRepositoryCustom
import org.test.kotlin_base.domain.sample.model.SampleEntity

interface SampleRepository : JpaRepository<SampleEntity, Long>, SampleRepositoryCustom {
}
