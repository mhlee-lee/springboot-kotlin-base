package org.test.kotlin_base.application.port.`in`.sample

import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleCommand
import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleResult
import org.test.kotlin_base.domain.addressscope.model.AddressScope
import org.test.kotlin_base.domain.sample.model.Sample

interface SampleUseCase {
    suspend fun getSamples(): List<Sample>

    suspend fun getAddressScopes(): List<AddressScope>

    suspend fun putSample(command: PutSampleCommand): PutSampleResult
}
