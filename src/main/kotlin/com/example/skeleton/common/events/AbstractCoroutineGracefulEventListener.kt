package com.ktcloud.kcp.cm.common.config.events

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.context.SmartLifecycle
import java.lang.Runnable
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

/**
 * @author MooHee Lee
 */
abstract class AbstractCoroutineGracefulEventListener(
    private val listenerName: String,
) : SmartLifecycle {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val running = AtomicBoolean(false)
    private val scope = CoroutineScope(SupervisorJob() + CoroutineName(listenerName))

    protected fun launchGracefully(
        eventName: String,
        context: CoroutineContext = EmptyCoroutineContext,
        process: suspend () -> Unit,
    ) {
        if (!running.get()) {
            logger.error("listener={}, event={}: scope already stopped, event dropped", listenerName, eventName)
            return
        }

        scope.launch(context) {
            try {
                process()
            } catch (e: CancellationException) {
                throw e // 셧다운 취소 신호는 삼키지 않는다
            } catch (@Suppress("TooGenericExceptionCaught") e: RuntimeException) {
                logger.error("listener={}, event={}: failed to process event", listenerName, eventName, e)
            }
        }
    }

    override fun start() {
        running.set(true)
        logger.info("listener={}: started", listenerName)
    }

    override fun stop(callback: Runnable) {
        running.set(false)

        val inFlight = scope.coroutineContext.job.children.toList()

        runCatching {
            if (inFlight.isNotEmpty()) {
                val finished = runBlocking {
                    withTimeoutOrNull(STOP_TIMEOUT_DURATION) { inFlight.joinAll() }
                }

                if (finished == null) {
                    logger.warn(
                        "listener={}: jobs did not finish within {}, cancelling",
                        listenerName,
                        STOP_TIMEOUT_DURATION,
                    )
                }
            }
        }.getOrElse {
            logger.error("listener={}: error while waiting for in-flight jobs", listenerName, it)
        }.also {
            scope.cancel()
            callback.run()
        }
    }

    // 구버전 호환용
    override fun stop() {
        stop { }
    }

    override fun isRunning(): Boolean = running.get()

    // WebServer(대략 MAX_VALUE - 1024)보다 낮은 phase → 웹 드레인 후 정지
    override fun getPhase(): Int = SmartLifecycle.DEFAULT_PHASE - WEB_SERVER_DRAIN_PHASE_OFFSET

    override fun isAutoStartup(): Boolean = true

    companion object {
        private val STOP_TIMEOUT_DURATION = 25.seconds
        private const val WEB_SERVER_DRAIN_PHASE_OFFSET = 1025
    }
}
