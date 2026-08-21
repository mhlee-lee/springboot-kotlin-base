package com.example.skeleton.common.extensions

import com.example.skeleton.common.config.TraceIdWebFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import kotlin.test.assertEquals

class CoroutineExtensionTests {
    @Test
    fun `async children preserve MDC across dispatcher switches`() = runBlocking {
        val traceId = "async-mdc-trace-id"
        MDC.put(TraceIdWebFilter.MDC_TRACE_ID_KEY, traceId)

        try {
            val result = withContext(MDCContext()) {
                asyncAndAwait(
                    { withContext(Dispatchers.Default) { MDC.get(TraceIdWebFilter.MDC_TRACE_ID_KEY) } },
                    { withContext(Dispatchers.IO) { MDC.get(TraceIdWebFilter.MDC_TRACE_ID_KEY) } },
                )
            }

            assertEquals(traceId to traceId, result)
        } finally {
            MDC.remove(TraceIdWebFilter.MDC_TRACE_ID_KEY)
        }
    }
}
