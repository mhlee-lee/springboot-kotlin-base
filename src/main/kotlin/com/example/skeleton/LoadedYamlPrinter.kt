package com.example.skeleton

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.stereotype.Component

/**
 * @author MooHee Lee
 */

@Component
@ConditionalOnProperty(
    prefix = "app.config",
    name = ["print-yaml-on-startup"],
    havingValue = "true",
    matchIfMissing = false, // 설정 없으면 동작 안 함 (default false)
)
class LoadedYamlPrinter(
    private val environment: ConfigurableEnvironment,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val sensitiveKeywords = listOf(
        "password", "passwd", "secret", "token", "credential",
        "api-key", "apikey", "private-key", "authorization"
    )

    @EventListener(ApplicationReadyEvent::class)
    fun printLoadedYaml() {
        log.info("===== Loaded application.yaml / yml sources =====")
        log.info("activeProfiles={}", environment.activeProfiles.joinToString().ifBlank { "(default)" })

        environment.propertySources
            .filter { isApplicationConfigSource(it.name) }
            .forEach { source ->
                log.info("----- source: {} -----", source.name)

                if (source !is EnumerablePropertySource<*>) {
                    log.info("  (enumerable 하지 않음)")
                    return@forEach
                }

                source.propertyNames
                    .sorted()
                    .forEach { key ->
                        val raw = source.getProperty(key)
                        val resolved = environment.getProperty(key)
                        val displayed = maskIfSensitive(key, resolved)

                        if (raw?.toString() == resolved) {
                            log.info("  {}={}", key, displayed)
                        } else {
                            // yaml 원본값과 최종 해석값이 다른 경우 (placeholder / override)
                            log.info("  {}={}  (resolved={})", key, raw, displayed)
                        }
                    }
            }

        log.info("===== end =====")
    }

    private fun isApplicationConfigSource(name: String): Boolean {
        val n = name.lowercase()
        return n.contains("applicationconfig") ||
            n.contains("config resource") && (n.contains(".yaml") || n.contains(".yml") || n.contains("application"))
    }

    private fun maskIfSensitive(key: String, value: String?): String {
        val lower = key.lowercase()
        if (sensitiveKeywords.none { lower.contains(it) }) {
            return value ?: "null"
        }
        return maskKeepPrefix(value)
    }

    private fun maskKeepPrefix(value: String?, visiblePrefix: Int = 2): String {
        if (value.isNullOrEmpty()) return ""
        if (value.length <= visiblePrefix) {
            return "*".repeat(value.length)
        }
        return value.take(visiblePrefix) + "*".repeat(value.length - visiblePrefix)
    }
}
