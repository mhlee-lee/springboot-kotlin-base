package org.test.kotlin_base.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.test.kotlin_base.application.port.input.sample.SampleUseCase
import org.test.kotlin_base.application.port.input.sample.model.PutSampleCommand
import org.test.kotlin_base.application.port.input.sample.model.PutSampleResult
import org.test.kotlin_base.application.port.input.sample.model.ValidateSampleCommand
import org.test.kotlin_base.application.port.input.sample.model.ValidateSampleResult
import org.test.kotlin_base.application.port.output.addressscope.LoadAddressScopePort
import org.test.kotlin_base.application.port.output.sample.LoadSamplePort
import org.test.kotlin_base.application.port.output.transaction.TransactionalPort
import org.test.kotlin_base.common.extensions.toJson
import org.test.kotlin_base.domain.addressscope.model.AddressScope
import org.test.kotlin_base.domain.sample.model.Sample

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

    override suspend fun putSample(command: PutSampleCommand): PutSampleResult {
        return PutSampleResult(
            name = command.name,
            age = command.age,
            gender = command.gender,
            id = command.id,
            ttl = command.ttl,
        )
    }

    override suspend fun validateSample(command: ValidateSampleCommand): ValidateSampleResult {
        return ValidateSampleResult(
            quantity = command.quantity,
            name = command.name,
            requiredValue = command.requiredValue,
            code = command.code,
        )
    }
}
