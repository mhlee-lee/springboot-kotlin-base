package org.test.kotlin_base.domain.addressScope

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.test.kotlin_base.domain.addressScope.model.AddressScope

interface AddressScopeRepository : CoroutineCrudRepository<AddressScope, String>
