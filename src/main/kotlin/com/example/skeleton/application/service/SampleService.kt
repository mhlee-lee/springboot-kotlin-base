package com.example.skeleton.application.service

import com.example.skeleton.application.port.input.sample.SampleUseCase
import com.example.skeleton.application.port.input.sample.model.CreateSampleCommand
import com.example.skeleton.application.port.input.sample.model.SampleSearchQuery
import com.example.skeleton.application.port.input.sample.model.UpdateSampleCommand
import com.example.skeleton.application.port.output.sample.SamplePort
import com.example.skeleton.application.port.output.transaction.TransactionalPort
import com.example.skeleton.common.exception.SampleNotFoundException
import com.example.skeleton.domain.sample.model.Sample
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SampleService(private val transactionalPort: TransactionalPort, private val samplePort: SamplePort) :
    SampleUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun searchSamples(query: SampleSearchQuery): List<Sample> = transactionalPort.executeReadOnly {
        log.debug("searchSamples() - query={}", query)
        samplePort.findByFilter(query.name, query.minAge, query.maxAge)
    }

    override suspend fun getSample(id: Long): Sample = transactionalPort.executeReadOnly {
        samplePort.findById(id)
    } ?: throw SampleNotFoundException(id)

    override suspend fun createSample(command: CreateSampleCommand): Sample = transactionalPort.execute {
        log.debug("createSample() - command={}", command)
        samplePort.insert(command.name, command.age)
    }

    override suspend fun updateSample(command: UpdateSampleCommand): Sample = transactionalPort.execute {
        log.debug("updateSample() - command={}, modifiedBy={}", command, command.modifiedBy)
        if (!samplePort.update(command.id, command.name, command.age)) {
            throw SampleNotFoundException(command.id)
        }
        samplePort.findById(command.id)!!
    }

    override suspend fun deleteSample(id: Long) {
        transactionalPort.execute {
            log.debug("deleteSample() - id={}", id)
            if (!samplePort.delete(id)) {
                throw SampleNotFoundException(id)
            }
        }
    }
}
