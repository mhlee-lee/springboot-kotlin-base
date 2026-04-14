package org.test.kotlin_base.adapter.output.persistence.config

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Executors

@Configuration
class DatabasePersistenceConfiguration {

    @Bean("databaseCoroutineDispatcher", destroyMethod = "close")
    fun databaseCoroutineDispatcher(): ExecutorCoroutineDispatcher {
        return Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    }

    @Bean("writeTransactionTemplate")
    fun writeTransactionTemplate(
        transactionManager: PlatformTransactionManager,
    ): TransactionTemplate {
        return transactionTemplate(transactionManager, readOnly = false)
    }

    @Bean("readTransactionTemplate")
    fun readTransactionTemplate(
        transactionManager: PlatformTransactionManager,
    ): TransactionTemplate {
        return transactionTemplate(transactionManager, readOnly = true)
    }

    private fun transactionTemplate(
        @Qualifier("transactionManager")
        transactionManager: PlatformTransactionManager,
        readOnly: Boolean,
    ): TransactionTemplate {
        return TransactionTemplate(transactionManager).apply {
            isReadOnly = readOnly
        }
    }
}
