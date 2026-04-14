package org.test.kotlin_base.adapter.output.persistence.jooq.addressscope

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.test.kotlin_base.application.port.output.addressscope.LoadAddressScopePort
import org.test.kotlin_base.domain.addressscope.model.AddressScope
import org.test.kotlin_base.domain.addressscope.model.AddressType
import org.test.kotlin_base.jooq.generated.tables.AddressScopes.Companion.ADDRESS_SCOPES

@Component
class AddressScopePersistenceAdapter(private val dslContext: DSLContext) : LoadAddressScopePort {
    override fun findAll(): List<AddressScope> = dslContext.select(
        ADDRESS_SCOPES.ID,
        ADDRESS_SCOPES.VPC_ID,
        ADDRESS_SCOPES.STATUS,
        ADDRESS_SCOPES.ADDRESS_TYPE,
        ADDRESS_SCOPES.CREATED_AT,
        ADDRESS_SCOPES.UPDATED_AT,
    )
        .from(ADDRESS_SCOPES)
        .orderBy(ADDRESS_SCOPES.ID.asc())
        .fetch { record ->
            AddressScope(
                id = requireNotNull(record[ADDRESS_SCOPES.ID]),
                vpcId = requireNotNull(record[ADDRESS_SCOPES.VPC_ID]),
                status = record[ADDRESS_SCOPES.STATUS],
                addressType = record[ADDRESS_SCOPES.ADDRESS_TYPE]
                    ?.let(AddressType::fromValue)
                    ?: throw IllegalStateException("address_type must not be null"),
                created = requireNotNull(record[ADDRESS_SCOPES.CREATED_AT]),
                updated = requireNotNull(record[ADDRESS_SCOPES.UPDATED_AT]),
            )
        }
}
