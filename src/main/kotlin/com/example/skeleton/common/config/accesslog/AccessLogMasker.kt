package com.example.skeleton.common.config.accesslog

import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.node.ObjectNode
import java.nio.charset.Charset

class AccessLogMasker(
    private val objectMapper: JsonMapper,
    maskedKeys: Set<String>,
    private val replacement: String = DEFAULT_REPLACEMENT,
) {
    private val normalizedKeys = maskedKeys.mapTo(mutableSetOf()) { it.lowercase() }

    fun maskJson(bytes: ByteArray, charset: Charset): Any = runCatching {
        objectMapper.readTree(String(bytes, charset)).also(::mask)
    }.getOrElse { "" }

    private fun mask(node: JsonNode) {
        when (node) {
            is ObjectNode -> node.properties().toList().forEach { (name, child) ->
                if (name.lowercase() in normalizedKeys) {
                    node.put(name, replacement)
                } else {
                    mask(child)
                }
            }

            is ArrayNode -> node.forEach(::mask)
        }
    }

    companion object {
        const val DEFAULT_REPLACEMENT = "[masked value]"
    }
}
