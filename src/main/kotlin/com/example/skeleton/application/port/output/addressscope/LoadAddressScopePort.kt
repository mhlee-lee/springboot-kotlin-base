package com.example.skeleton.application.port.output.addressscope

import com.example.skeleton.domain.addressscope.model.AddressScope

interface LoadAddressScopePort {
    fun findAll(): List<AddressScope>
}
