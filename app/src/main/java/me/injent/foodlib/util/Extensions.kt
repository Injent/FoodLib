package me.injent.foodlib.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

fun Instant.format(patter: String): String {
    val formatter = DateTimeFormatter.ofPattern(patter)
    val date = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return formatter.format(date.toJavaLocalDateTime())
}