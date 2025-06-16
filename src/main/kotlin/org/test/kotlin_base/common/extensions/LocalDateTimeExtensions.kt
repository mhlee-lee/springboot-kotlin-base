package org.test.kotlin_base.common.extensions

import org.test.kotlin_base.common.enums.DatePatternEnum
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun LocalDateTime.atStartOfHour(): LocalDateTime = with(LocalTime.of(this.toLocalTime().hour, 0, 0))
fun LocalDateTime.atEndOfHour(): LocalDateTime = with(LocalTime.of(this.toLocalTime().hour, 59, 59))
fun LocalDateTime.atEndOfDay(): LocalDateTime = with(LocalTime.of(23, 59, 59))
fun LocalDateTime.atMidNight(): LocalDateTime = with(LocalTime.of(0, 0, 0))
fun LocalDate.atEndOfDay(): LocalDateTime = LocalDateTime.of(this, LocalTime.of(23, 59, 59))

fun LocalDateTime.setTime(hour: Int, minute: Int, second: Int): LocalDateTime {
    return this.with(LocalTime.of(hour, minute, second))
}

fun LocalDateTime.toString(datePattern: DatePatternEnum): String {
    return this.format(datePattern.formatter)
}

fun LocalDate.toString(datePattern: DatePatternEnum): String {
    return this.format(datePattern.formatter)
}
