package com.example.skeleton.domain.transaction

interface TransactionalPort {
    suspend fun <T> execute(block: () -> T): T

    suspend fun <T> executeReadOnly(block: () -> T): T
}
