package org.test.kotlin_base.adapter.out.persistence.jpa.addressscope

import org.springframework.stereotype.Component
import org.test.kotlin_base.adapter.out.persistence.jpa.addressscope.entity.toDomain
import org.test.kotlin_base.application.port.out.addressscope.LoadAddressScopePort
import org.test.kotlin_base.domain.addressscope.model.AddressScope

@Component
class AddressScopePersistenceAdapter(
    private val addressScopeJpaRepository: AddressScopeJpaRepository,
) : LoadAddressScopePort {
    override fun findAll(): List<AddressScope> {
        return addressScopeJpaRepository.findAll()
            .map { it.toDomain() }
    }
}
