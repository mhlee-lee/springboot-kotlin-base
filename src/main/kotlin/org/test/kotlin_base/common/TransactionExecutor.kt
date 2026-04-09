package org.test.kotlin_base.common

import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.test.kotlin_base.infrastructure.database.configuration.DatabaseRoute
import org.test.kotlin_base.infrastructure.database.configuration.DatabaseRouteContext
import reactor.util.context.Context

@Component
class TransactionExecutor(
    @Qualifier("writeTransactionalOperator")
    private val writeTransactionalOperator: TransactionalOperator,
    @Qualifier("readTransactionalOperator")
    private val readTransactionalOperator: TransactionalOperator,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun <T : Any> execute(block: suspend () -> T): T {
        log.debug("execute() - route={}", DatabaseRoute.WRITE)
        return runInTransaction(writeTransactionalOperator, DatabaseRoute.WRITE, block)
    }

    suspend fun <T : Any> executeReadonly(block: suspend () -> T): T {
        log.debug("executeReadonly() - route={}", DatabaseRoute.READ)
        return runInTransaction(readTransactionalOperator, DatabaseRoute.READ, block)
    }

    private suspend fun <T : Any> runInTransaction(
        transactionalOperator: TransactionalOperator,
        route: DatabaseRoute,
        block: suspend () -> T,
    ): T {
        return withContext(Context.of(DatabaseRouteContext.KEY, route).asCoroutineContext()) {
            transactionalOperator.executeAndAwait {
                block()
            }
        }
    }
}
