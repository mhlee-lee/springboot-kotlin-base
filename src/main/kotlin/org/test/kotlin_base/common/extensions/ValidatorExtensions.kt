package org.test.kotlin_base.common.extensions

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator

fun Validator.validateOrThrow(target: Any) {
    val errors = if (target is Collection<*>) target.asSequence().map { validate(it) }.flatten().toSet()
    else validate(target)
    if (errors.isNotEmpty()) throw ConstraintViolationException(errors)
}
