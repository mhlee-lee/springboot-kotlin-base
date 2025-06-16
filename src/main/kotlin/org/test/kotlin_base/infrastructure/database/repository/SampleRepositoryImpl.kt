package org.test.kotlin_base.infrastructure.database.repository

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.test.kotlin_base.infrastructure.database.repository.SampleRepositoryCustom
import org.test.kotlin_base.domain.sample.model.SampleEntity

@Repository
class SampleRepositoryImpl : QuerydslRepositorySupport(SampleEntity::class.java), SampleRepositoryCustom {
}
