package org.test.kotlin_base.application.port.input.sample

import org.test.kotlin_base.application.port.input.sample.model.PutSampleCommand
import org.test.kotlin_base.application.port.input.sample.model.PutSampleResult
import org.test.kotlin_base.application.port.input.sample.model.ValidateSampleCommand
import org.test.kotlin_base.application.port.input.sample.model.ValidateSampleResult
import org.test.kotlin_base.domain.addressscope.model.AddressScope
import org.test.kotlin_base.domain.sample.model.Sample

interface SampleUseCase {
    suspend fun getSamples(): List<Sample>

    suspend fun getAddressScopes(): List<AddressScope>

    suspend fun putSample(command: PutSampleCommand): PutSampleResult

    suspend fun validateSample(command: ValidateSampleCommand): ValidateSampleResult
}
