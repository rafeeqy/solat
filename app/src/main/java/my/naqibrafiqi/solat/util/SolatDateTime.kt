package my.naqibrafiqi.solat.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object SolatDateTime {

    fun fromEpochSecondToLocalTime(epochSecond: Long): LocalTime {
        return Instant.ofEpochSecond(epochSecond)
            .atZone(ZoneId.of("Asia/Kuala_Lumpur"))
            .toLocalTime()
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

    fun formatLocalDate(date: LocalDate, format: String = "dd/MM/uuuu"): String {
        return date.format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    }

    fun formatLocalTime(time: LocalTime, format: String = "h:mm a"): String {
        return time.format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    }

    // TODO: The functions below are to be reviewed...

    fun nowEpochMilli(): Long {
        return Instant.now().toEpochMilli()
    }

    fun tomorrowEpochMilli(): Long {
        return Instant.now().plus(Duration.ofDays(1)).toEpochMilli()
    }

    fun splitDate(epochMilli: Long): ExtractedDateTime {
        val split = formatDate(
            date = epochMilli,
            format = "uuuu-MM-dd-hh-HH-mm-ss-a"
        ).split("-")

        return ExtractedDateTime(
            year = split[0].toInt(),
            month = split[1].toInt(),
            day = split[2].toInt(),
            hour = split[3].toInt(),
            hour24 = split[4].toInt(),
            minute = split[5].toInt(),
            second = split[6].toInt(),
            isAfternoon = split[7] == "PM"
        )
    }

    fun nextDayStatus(): NextDayStatus {
        val instant = Instant.now()
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val nextDay = localDateTime.plusDays(1)

        return when {
            nextDay.year > localDateTime.year -> NextDayStatus.NEXT_YEAR

            nextDay.month > localDateTime.month -> NextDayStatus.NEXT_MONTH

            else -> NextDayStatus.NO_CHANGES
        }
    }
}

data class ExtractedDateTime(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val hour24: Int,
    val minute: Int,
    val second: Int,
    val isAfternoon: Boolean
)

enum class NextDayStatus {
    NO_CHANGES, NEXT_MONTH, NEXT_YEAR
}
