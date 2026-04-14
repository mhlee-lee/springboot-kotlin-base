package org.test.kotlin_base.adapter.out.persistence.r2dbc.addressscope

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.test.kotlin_base.adapter.out.persistence.r2dbc.addressscope.entity.toDomain
import org.test.kotlin_base.application.port.out.addressscope.LoadAddressScopePort
import org.test.kotlin_base.domain.addressscope.model.AddressScope

@Component
class AddressScopePersistenceAdapter(
    private val addressScopeR2dbcRepository: AddressScopeR2dbcRepository,
) : LoadAddressScopePort {
    override suspend fun findAll(): List<AddressScope> {
        return addressScopeR2dbcRepository.findAll()
            .toList()
            .map { it.toDomain() }
    }
}
