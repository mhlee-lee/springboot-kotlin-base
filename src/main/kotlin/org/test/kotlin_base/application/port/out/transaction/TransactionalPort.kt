package org.test.kotlin_base.application.port.out.transaction

interface TransactionalPort {
    suspend fun <T : Any> execute(block: suspend () -> T): T

    suspend fun <T : Any> executeReadOnly(block: suspend () -> T): T
}
