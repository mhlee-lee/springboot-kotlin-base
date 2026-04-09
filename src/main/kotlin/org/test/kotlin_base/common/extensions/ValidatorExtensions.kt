package org.test.kotlin_base.common.extensions

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody

private val defaultValidator: Validator = Validation.buildDefaultValidatorFactory().validator

fun <T : Any> Validator.validateOrThrow(target: T): T {
    val violations = if (target is Collection<*>) target.asSequence().flatMap { validate(it) }.toSet()
    else validate(target)

    if (violations.isNotEmpty()) throw ConstraintViolationException(violations)
    return target
}

fun <T : Any> T.validate(validator: Validator = defaultValidator): T = validator.validateOrThrow(this)

suspend inline fun <reified T : Any> ServerRequest.awaitBodyValidated(validator: Validator): T =
    validator.validateOrThrow(awaitBody<T>())
