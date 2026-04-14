package org.test.kotlin_base.adapter.out.persistence.jpa.addressscope

import org.springframework.data.jpa.repository.JpaRepository
import org.test.kotlin_base.adapter.out.persistence.jpa.addressscope.entity.AddressScopeEntity

interface AddressScopeJpaRepository : JpaRepository<AddressScopeEntity, String>
