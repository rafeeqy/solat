package com.example.solat.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter

object SolatDateTime {
    fun nowEpochMilli(): Long {
        return Instant.now().toEpochMilli()
    }

    fun tomorrowEpochMilli(): Long {
        return Instant.now().plus(Duration.ofDays(1)).toEpochMilli()
    }

    fun formatDate(
        date: Long,
        format: String = "EEEE, dd MMMM uuuu"
    ): String {
        val instant = Instant.ofEpochMilli(date)

        val formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault())

        return formatter.format(instant)
    }

    fun formatHijrahDate(
        date: Long,
        format: String = "EEEE, dd MMMM uuuu"
    ): String {
        val instant = Instant.ofEpochMilli(date)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())

        val hijrahDate: HijrahDate = HijrahChronology.INSTANCE.date(zonedDateTime)

        val formatter = DateTimeFormatter.ofPattern(format)
        val formattedDate = hijrahDate.format(formatter.withZone(ZoneId.systemDefault()))

        return formattedDate
    }
}
