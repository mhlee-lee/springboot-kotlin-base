package org.test.kotlin_base.adapter.out.persistence.jpa.addressscope

import jakarta.persistence.Converter
import org.test.kotlin_base.adapter.out.persistence.jpa.config.GenericEnumConverter
import org.test.kotlin_base.domain.addressscope.model.AddressType

@Converter
class AddressTypeConverter : GenericEnumConverter<AddressType>(AddressType::class.java)
