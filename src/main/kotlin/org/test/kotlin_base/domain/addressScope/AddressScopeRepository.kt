package org.test.kotlin_base.domain.addressScope

import org.springframework.data.jpa.repository.JpaRepository
import org.test.kotlin_base.domain.addressScope.model.AddressScope

interface AddressScopeRepository : JpaRepository<AddressScope, String> {
}
