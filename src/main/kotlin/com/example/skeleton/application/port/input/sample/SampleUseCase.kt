package com.example.skeleton.application.port.input.sample

import com.example.skeleton.application.port.input.sample.model.PutSampleCommand
import com.example.skeleton.application.port.input.sample.model.PutSampleResult
import com.example.skeleton.application.port.input.sample.model.ValidateSampleCommand
import com.example.skeleton.application.port.input.sample.model.ValidateSampleResult
import com.example.skeleton.domain.addressscope.model.AddressScope
import com.example.skeleton.domain.sample.model.Sample

interface SampleUseCase {
    suspend fun getSamples(): List<Sample>

    suspend fun getAddressScopes(): List<AddressScope>

    suspend fun putSample(command: PutSampleCommand): PutSampleResult

    suspend fun validateSample(command: ValidateSampleCommand): ValidateSampleResult
}
