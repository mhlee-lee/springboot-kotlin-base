package org.test.kotlin_base.adapter.out.persistence.r2dbc.addressscope

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.test.kotlin_base.adapter.out.persistence.r2dbc.addressscope.entity.AddressScopeEntity

interface AddressScopeR2dbcRepository : CoroutineCrudRepository<AddressScopeEntity, String>
