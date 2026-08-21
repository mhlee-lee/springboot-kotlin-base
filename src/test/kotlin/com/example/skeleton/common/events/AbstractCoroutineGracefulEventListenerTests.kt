package com.example.skeleton.common.events

import com.example.skeleton.common.config.TraceIdWebFilter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class AbstractCoroutineGracefulEventListenerTests {
    @Test
    fun `launched event preserves caller MDC`() = runBlocking {
        val listener = TestEventListener().apply { start() }
        val traceId = "event-mdc-trace-id"
        val propagatedTraceId = CompletableDeferred<String?>()
        MDC.put(TraceIdWebFilter.MDC_TRACE_ID_KEY, traceId)

        try {
            listener.submit {
                propagatedTraceId.complete(MDC.get(TraceIdWebFilter.MDC_TRACE_ID_KEY))
            }

            assertEquals(traceId, withTimeout(5.seconds) { propagatedTraceId.await() })
        } finally {
            MDC.remove(TraceIdWebFilter.MDC_TRACE_ID_KEY)
            listener.stop()
        }
    }

    private class TestEventListener : AbstractCoroutineGracefulEventListener("test-event-listener") {
        fun submit(process: suspend () -> Unit) {
            launchGracefully(eventName = "test-event", process = process)
        }
    }
}
