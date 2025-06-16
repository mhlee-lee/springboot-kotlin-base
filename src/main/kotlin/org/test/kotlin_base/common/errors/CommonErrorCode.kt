package org.test.kotlin_base.common.errors

enum class CommonErrorCode(override val code: String, override val label: String) : ErrorCode {
    INTERNAL_SERVER_ERROR("CEC0001", "common.CommonErrorCode.INTERNAL_SERVER_ERROR"),
    BAD_REQUEST("CEC0002", "common.CommonErrorCode.BAD_REQUEST"),
    UNAUTHORIZED("CEC0003", "common.CommonErrorCode.UNAUTHORIZED"),
    FORBIDDEN("CEC0004", "common.CommonErrorCode.FORBIDDEN"),
    NOT_FOUND("CEC0005", "common.CommonErrorCode.NOT_FOUND"),
    METHOD_NOT_ALLOWED("CEC0006", "common.CommonErrorCode.METHOD_NOT_ALLOWED"),
    NOT_ACCEPTABLE("CEC0007", "common.CommonErrorCode.NOT_ACCEPTABLE"),
    UNSUPPORTED_MEDIA_TYPE("CEC0008", "common.CommonErrorCode.UNSUPPORTED_MEDIA_TYPE"),
    INVALID_TOKEN("CEC0009", "common.CommonErrorCode.INVALID_TOKEN"),
    INVALID_HEADER_PARAMETER("CEC0010", "common.CommonErrorCode.INVALID_HEADER_PARAMETER"),
    VALIDATION_FAIL("CEC0011", "common.CommonErrorCode.VALIDATION_FAIL"),
    EMPTY_BODY("CEC0012", "common.CommonErrorCode.EMPTY_BODY"),
    INVALID_PARAMETER("CEC0013", "common.CommonErrorCode.INVALID_PARAMETER"),
    PAYLOAD_TOO_LARGE("CEC0014", "common.CommonErrorCode.PAYLOAD_TOO_LARGE"),
    JSON_PARSE_ERROR("CEC0015", "common.CommonErrorCode.JSON_PARSE_ERROR"),
    NOT_NULL("CEC0016", "common.CommonErrorCode.NOT_NULL"),
    INVALID_FORMAT("CEC0017", "common.CommonErrorCode.INVALID_FORMAT"),
    MISMATCH("CEC0018", "common.CommonErrorCode.MISMATCH"),
    ;
}
