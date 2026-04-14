package org.test.kotlin_base.application.port.output.addressscope

import org.test.kotlin_base.domain.addressscope.model.AddressScope

interface LoadAddressScopePort {
    fun findAll(): List<AddressScope>
}
