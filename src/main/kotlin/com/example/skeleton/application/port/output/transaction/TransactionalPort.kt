package com.example.skeleton.application.port.output.transaction

interface TransactionalPort {
    suspend fun <T> execute(block: () -> T): T

    suspend fun <T> executeReadOnly(block: () -> T): T
}
