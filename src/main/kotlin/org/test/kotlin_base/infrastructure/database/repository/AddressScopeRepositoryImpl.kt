package org.test.kotlin_base.infrastructure.database.repository

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.test.kotlin_base.domain.addressScope.model.AddressScope

@Repository
class AddressScopeRepositoryImpl : QuerydslRepositorySupport(AddressScope::class.java) {
}
