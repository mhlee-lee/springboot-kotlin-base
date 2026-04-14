package org.test.kotlin_base.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.test.kotlin_base.application.port.`in`.sample.SampleUseCase
import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleCommand
import org.test.kotlin_base.application.port.`in`.sample.model.PutSampleResult
import org.test.kotlin_base.application.port.`in`.sample.model.ValidateSampleCommand
import org.test.kotlin_base.application.port.`in`.sample.model.ValidateSampleResult
import org.test.kotlin_base.application.port.out.addressscope.LoadAddressScopePort
import org.test.kotlin_base.application.port.out.sample.LoadSamplePort
import org.test.kotlin_base.application.port.out.transaction.TransactionalPort
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
        val writeSamples = transactionalPort.execute {
            loadSamplePort.findAll()
        }
        val readSamples = transactionalPort.executeReadOnly {
            loadSamplePort.findAll()
        }

        log.info("writeSamples: {}", writeSamples.toJson())
        log.info("readSamples: {}", readSamples.toJson())

        return readSamples
    }

    override suspend fun getAddressScopes(): List<AddressScope> {
        val writeAddressScopes = transactionalPort.execute {
            loadAddressScopePort.findAll()
        }
        val readAddressScopes = transactionalPort.executeReadOnly {
            loadAddressScopePort.findAll()
        }

        log.info("writeAddressScopes: {}", writeAddressScopes.toJson())
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
