package org.test.kotlin_base.application.port.output.transaction

interface TransactionalPort {
    suspend fun <T> execute(block: () -> T): T

    suspend fun <T> executeReadOnly(block: () -> T): T
}
