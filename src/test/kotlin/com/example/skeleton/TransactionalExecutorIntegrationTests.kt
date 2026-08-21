package com.example.skeleton

import com.example.skeleton.common.config.TraceIdWebFilter
import com.example.skeleton.domain.transaction.TransactionalPort
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class TransactionalExecutorIntegrationTests(@Autowired private val transactionalPort: TransactionalPort) {
    @Test
    fun `write transaction block runs on virtual thread`() = runBlocking {
        val isVirtualThread = transactionalPort.execute {
            Thread.currentThread().isVirtual
        }

        assertTrue(isVirtualThread)
    }

    @Test
    fun `read only transaction block runs on virtual thread`() = runBlocking {
        val isVirtualThread = transactionalPort.executeReadOnly {
            Thread.currentThread().isVirtual
        }

        assertTrue(isVirtualThread)
    }

    @Test
    fun `transaction block preserves MDC across dispatcher switch`() = runBlocking {
        val traceId = "transaction-mdc-trace-id"
        MDC.put(TraceIdWebFilter.MDC_TRACE_ID_KEY, traceId)

        try {
            val propagatedTraceId = withContext(MDCContext()) {
                transactionalPort.execute {
                    MDC.get(TraceIdWebFilter.MDC_TRACE_ID_KEY)
                }
            }

            assertEquals(traceId, propagatedTraceId)
        } finally {
            MDC.remove(TraceIdWebFilter.MDC_TRACE_ID_KEY)
        }
    }
}
