package org.test.kotlin_base.adapter.out.transaction

import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.test.kotlin_base.adapter.out.persistence.r2dbc.config.DatabaseRoute
import org.test.kotlin_base.adapter.out.persistence.r2dbc.config.DatabaseRouteContext
import org.test.kotlin_base.application.port.out.transaction.TransactionalPort
import reactor.util.context.Context

@Component
class TransactionalExecutorAdapter(
    @Qualifier("writeTransactionalOperator")
    private val writeTransactionalOperator: TransactionalOperator,
    @Qualifier("readTransactionalOperator")
    private val readTransactionalOperator: TransactionalOperator,
) : TransactionalPort {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun <T> execute(block: suspend () -> T): T {
        log.debug("execute() - route={}", DatabaseRoute.WRITE)
        return runInTransaction(writeTransactionalOperator, DatabaseRoute.WRITE, block)
    }

    override suspend fun <T> executeReadOnly(block: suspend () -> T): T {
        log.debug("executeReadonly() - route={}", DatabaseRoute.READ)
        return runInTransaction(readTransactionalOperator, DatabaseRoute.READ, block)
    }

    private suspend fun <T> runInTransaction(
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
