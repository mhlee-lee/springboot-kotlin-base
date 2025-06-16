package org.test.kotlin_base.common

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class TransactionExecutor {
    private val log = LoggerFactory.getLogger(this::class.java)
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun <T> execute(block: () -> T): T {
        log.info("execute() - Transaction active: ${TransactionSynchronizationManager.isActualTransactionActive()}, ReadOnly: ${TransactionSynchronizationManager.isCurrentTransactionReadOnly()}, Thread: ${Thread.currentThread().name}")
        return block()
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    fun <T> executeReadonly(block: () -> T): T {
        log.info("executeReadonly() - Transaction active: ${TransactionSynchronizationManager.isActualTransactionActive()}, ReadOnly: ${TransactionSynchronizationManager.isCurrentTransactionReadOnly()}, Thread: ${Thread.currentThread().name}")
        return block()
    }
}
