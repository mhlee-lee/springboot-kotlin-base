package com.example.skeleton.application.service

import com.example.skeleton.application.port.input.sample.SampleUseCase
import com.example.skeleton.application.port.input.sample.model.PutSampleCommand
import com.example.skeleton.application.port.input.sample.model.PutSampleResult
import com.example.skeleton.application.port.input.sample.model.ValidateSampleCommand
import com.example.skeleton.application.port.input.sample.model.ValidateSampleResult
import com.example.skeleton.application.port.output.addressscope.LoadAddressScopePort
import com.example.skeleton.application.port.output.sample.LoadSamplePort
import com.example.skeleton.application.port.output.transaction.TransactionalPort
import com.example.skeleton.common.extensions.toJson
import com.example.skeleton.domain.addressscope.model.AddressScope
import com.example.skeleton.domain.sample.model.Sample
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SampleService(
    private val loadSamplePort: LoadSamplePort,
    private val loadAddressScopePort: LoadAddressScopePort,
    private val transactionalPort: TransactionalPort,
) : SampleUseCase {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun getSamples(): List<Sample> {
        val readSamples = transactionalPort.executeReadOnly {
            loadSamplePort.findAll()
        }

        log.info("readSamples: {}", readSamples.toJson())

        return readSamples
    }

    override suspend fun getAddressScopes(): List<AddressScope> {
        val readAddressScopes = transactionalPort.executeReadOnly {
            loadAddressScopePort.findAll()
        }

        log.info("readAddressScopes: {}", readAddressScopes.toJson())

        return readAddressScopes
    }

    override suspend fun putSample(command: PutSampleCommand): PutSampleResult = PutSampleResult(
        name = command.name,
        age = command.age,
        gender = command.gender,
        id = command.id,
        ttl = command.ttl,
    )

    override suspend fun validateSample(command: ValidateSampleCommand): ValidateSampleResult = ValidateSampleResult(
        quantity = command.quantity,
        name = command.name,
        requiredValue = command.requiredValue,
        code = command.code,
    )
}
