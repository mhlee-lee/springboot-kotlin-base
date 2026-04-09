package org.test.kotlin_base.presentation.sample

import jakarta.validation.Validator
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.test.kotlin_base.common.TransactionExecutor
import org.test.kotlin_base.common.errors.ApiFieldError
import org.test.kotlin_base.common.errors.CommonErrorCode
import org.test.kotlin_base.common.errors.ErrorSource
import org.test.kotlin_base.common.exception.RequestValidationException
import org.test.kotlin_base.common.extensions.toJson
import org.test.kotlin_base.common.extensions.validateOrThrow
import org.test.kotlin_base.domain.addressScope.AddressScopeRepository
import org.test.kotlin_base.domain.sample.SampleRepository
import org.test.kotlin_base.presentation.enums.Gender
import org.test.kotlin_base.presentation.sample.protocol.SampleRequest
import org.test.kotlin_base.presentation.sample.protocol.SampleResponse

@Component
class SampleHandler(
    private val sampleRepository: SampleRepository,
    private val addressScopeRepository: AddressScopeRepository,
    private val transactionExecutor: TransactionExecutor,
    private val validator: Validator,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun getSample(request: ServerRequest): ServerResponse {
        val value1 = transactionExecutor.execute {
            sampleRepository.findAll().toList()
        }
        val value2 = transactionExecutor.executeReadonly {
            sampleRepository.findAll().toList()
        }

        log.info("value1: ${value1.toJson()}")
        log.info("value2: ${value2.toJson()}")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun addressScope(request: ServerRequest): ServerResponse {
        val value1 = transactionExecutor.execute {
            addressScopeRepository.findAll().toList()
        }
        val value2 = transactionExecutor.executeReadonly {
            addressScopeRepository.findAll().toList()
        }

        log.info("value1: ${value1.toJson()}")
        log.info("value2: ${value2.toJson()}")

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun putSample(request: ServerRequest): ServerResponse {
        val age = request.headers().firstHeader("age")?.toIntOrNull()
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.INVALID_HEADER_PARAMETER,
                fieldError = fieldError(
                    source = ErrorSource.HEADER,
                    field = "age",
                    reason = if (request.headers().firstHeader("age") == null) "required" else "invalid_value",
                    message = CommonErrorCode.INVALID_HEADER_PARAMETER.getMessage(arrayOf("age")),
                ),
            )
        val name = request.queryParamOrNull("name")
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.INVALID_PARAMETER,
                fieldError = fieldError(
                    source = ErrorSource.QUERY,
                    field = "name",
                    reason = "required",
                    message = CommonErrorCode.INVALID_PARAMETER.getMessage(arrayOf("name")),
                ),
            )
        val gender = request.pathVariableOrNull("gender")
            ?.let { raw -> enumValues<Gender>().firstOrNull { it.name == raw } }
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.INVALID_PARAMETER,
                fieldError = fieldError(
                    source = ErrorSource.PATH,
                    field = "gender",
                    reason = "invalid_enum",
                    message = CommonErrorCode.INVALID_PARAMETER.getMessage(arrayOf("gender")),
                ),
            )
        val requestBody = request.awaitBodyOrNull<SampleRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw RequestValidationException.single(
                errorCode = CommonErrorCode.EMPTY_BODY,
                fieldError = fieldError(
                    source = ErrorSource.BODY,
                    field = "body",
                    reason = "required",
                    message = CommonErrorCode.EMPTY_BODY.getMessage(),
                ),
            )

        val response = SampleResponse(name, age, gender, requestBody.id, requestBody.ttl)

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    private fun fieldError(
        source: ErrorSource,
        field: String,
        reason: String,
        message: String,
    ): ApiFieldError {
        return ApiFieldError(
            source = source.wireName,
            field = field,
            reason = reason,
            message = message,
        )
    }
}
