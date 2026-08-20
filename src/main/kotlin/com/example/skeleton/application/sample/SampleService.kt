package com.example.skeleton.application.sample

import com.example.skeleton.application.sample.model.CreateSampleCommand
import com.example.skeleton.application.sample.model.SampleSearchQuery
import com.example.skeleton.application.sample.model.UpdateSampleCommand
import com.example.skeleton.common.exception.SampleNotFoundException
import com.example.skeleton.domain.sample.SampleRepository
import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.domain.sample.model.SampleStatus
import com.example.skeleton.domain.transaction.TransactionalPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SampleService(
    private val transactionalPort: TransactionalPort,
    private val samplePort: SampleRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun searchSamples(query: SampleSearchQuery): List<Sample> = transactionalPort.executeReadOnly {
        log.debug("searchSamples() - query={}", query)
        samplePort.findByFilter(query.name, query.minAge, query.maxAge, query.status)
    }

    suspend fun searchSamplesByStatus(status: SampleStatus): List<Sample> = transactionalPort.executeReadOnly {
        log.debug("searchSamplesByStatus() - status={}", status)
        samplePort.findByStatus(status)
    }

    suspend fun getSample(id: Long): Sample = transactionalPort.executeReadOnly {
        samplePort.findById(id)
    } ?: throw SampleNotFoundException(id)

    suspend fun createSample(command: CreateSampleCommand): Sample = transactionalPort.execute {
        log.debug("createSample() - command={}", command)
        samplePort.insert(command.name, command.age, command.status)
    }

    suspend fun updateSample(command: UpdateSampleCommand): Sample = transactionalPort.execute {
        log.debug("updateSample() - command={}, modifiedBy={}", command, command.modifiedBy)
        if (!samplePort.update(command.id, command.name, command.age, command.status)) {
            throw SampleNotFoundException(command.id)
        }
        samplePort.findById(command.id)!!
    }

    suspend fun deleteSample(id: Long) {
        transactionalPort.execute {
            log.debug("deleteSample() - id={}", id)
            if (!samplePort.delete(id)) {
                throw SampleNotFoundException(id)
            }
        }
    }
}
