package com.example.skeleton.common.extensions

import org.jooq.Field
import org.jooq.Record

/**
 * @author MooHee Lee
 */

inline fun <reified T> org.jooq.Record.getRequired(field: Field<T>): T & Any =
    this.get(field) ?: throw IllegalArgumentException("Field ${field.name} is null")

inline fun <reified T> Record.getNullable(field: Field<T>): T = this.get(field)
