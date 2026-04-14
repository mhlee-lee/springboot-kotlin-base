package org.test.kotlin_base.common.errors

data class ApiErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
    val path: String,
    val traceId: String?,
    val errors: List<ApiFieldError>? = null,
)

data class ApiFieldError(
    val source: String,
    val field: String,
    val reason: String,
    val message: String,
)

enum class ErrorSource(val wireName: String) {
    BODY("body"),
    QUERY("query"),
    PATH("path"),
    HEADER("header"),
}
