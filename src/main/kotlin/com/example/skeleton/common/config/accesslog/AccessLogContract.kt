package com.example.skeleton.common.config.accesslog

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("ktcloud.web-access-log")
data class AccessLogProperties(
    val maxBodySize: Int = DEFAULT_MAX_BODY_SIZE,
    val maskedKeys: Set<String> = DEFAULT_MASKED_KEYS,
    val adminPathPatterns: List<String> = emptyList(),
    val systemPathPatterns: List<String> = listOf("/actuator/**"),
) {
    init {
        require(maxBodySize in 1..MAX_BODY_SIZE) {
            "maxBodySize must be between 1 and $MAX_BODY_SIZE"
        }
    }

    companion object {
        const val DEFAULT_MAX_BODY_SIZE = 32 * 1024
        const val MAX_BODY_SIZE = 256 * 1024
        val DEFAULT_MASKED_KEYS = setOf(
            "password",
            "passwd",
            "pwd",
            "secret",
            "clientSecret",
            "accessToken",
            "refreshToken",
            "authorization",
            "email",
            "token",
        )
    }
}

data class RequestAccessLog(
    val ts: String,
    val host: String,
    val metd: String,
    val type: String,
    val ct: String,
    val uri: String,
    val qs: String,
    val body: Any,
    @get:JsonProperty("req_size")
    val reqSize: Long,
    val cli: AccessLogClient,
    val usr: AccessLogUser,
    val tid: String,
)

data class AccessLogClient(
    val ip: String,
    val agent: String,
)

data class AccessLogUser(
    val id: String,
    val org: String,
    val prj: String,
)

data class ResponseAccessLog(
    val ts: String,
    val code: Int,
    @get:JsonProperty("res_size")
    val resSize: Long,
    val dur: Long,
    val err: AccessLogError,
    val tid: String,
)

data class AccessLogError(
    val code: String = "",
    val msg: String = "",
)
