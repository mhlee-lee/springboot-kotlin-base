package org.test.kotlin_base.application.port.out.transaction

interface TransactionalPort {
    suspend fun <T> execute(block: suspend () -> T): T

    suspend fun <T> executeReadOnly(block: suspend () -> T): T
}
