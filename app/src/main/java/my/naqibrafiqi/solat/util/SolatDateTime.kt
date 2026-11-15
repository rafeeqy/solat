package my.naqibrafiqi.solat.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import my.naqibrafiqi.solat.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale

object SolatDateTime {

    /**
     * JAKIM prayer times are Malaysia time. Every date and time in the app is worked out in this
     * zone, whatever the phone's own time zone is, so the countdown and alarms match the timetable.
     */
    val ZONE: ZoneId = ZoneId.of("Asia/Kuala_Lumpur")

    fun nowEpochMilli(): Long {
        return Instant.now().toEpochMilli()
    }

    fun fromEpochSecondToLocalTime(epochSecond: Long): LocalTime {
        return Instant.ofEpochSecond(epochSecond)
            .atZone(ZONE)
            .toLocalTime()
    }

    /**
     * Format [date] (Epoch milliseconds) to Gregorian date of format `DDDD, D MMMM YYYY`, e.g. **Jumaat, 4 July 2026**
     *
     * @param date Epoch milliseconds
     * @return Gregorian date of format `DDDD, D MMMM YYYY`
     */
    @Composable
    fun formatToFullDate(date: Long): String {
        val months = stringArrayResource(id = R.array.months)
        val days = stringArrayResource(id = R.array.days_of_week)

        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZONE)
            .toLocalDate()

        val monthName = months[localDate.monthValue - 1]
        val dayName = days[localDate.dayOfWeek.value - 1]

        return "$dayName, ${localDate.dayOfMonth} $monthName ${localDate.year}"
    }

    /**
     * Format [date] (Epoch milliseconds) to Hijri date of format `D MMMM YYYY`, e.g. **19 Muharram 1448**
     *
     * @param date Epoch milliseconds
     * @return Hijri date of format `D MMMM YYYY`
     */
    @Composable
    fun formatToHijrahDate(date: Long): String {
        val hijriMonths = stringArrayResource(id = R.array.hijri_months)

        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZONE)
            .toLocalDate()

        val hijriDate = HijrahDate.from(localDate)

        val hDay = hijriDate.get(ChronoField.DAY_OF_MONTH)
        val hMonth = hijriMonths[hijriDate.get(ChronoField.MONTH_OF_YEAR) - 1]
        val hYear = hijriDate.get(ChronoField.YEAR_OF_ERA)

        return "$hDay $hMonth $hYear"
    }

    /**
     * Format [date] ([LocalDate]) to Gregorian date of format `DDDD, DD/MM/YYYY`, e.g. **Friday, 04/07/2026**
     *
     * @param date [LocalDate]
     * @return Gregorian date of format `DDDD, DD/MM/YYYY`
     */
    @Composable
    fun formatLocalDate(date: LocalDate): String {
        val days = stringArrayResource(id = R.array.days_of_week)

        val dayName = days[date.dayOfWeek.value - 1]

        val formattedDate = date.format(
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withZone(ZONE)
        )

        return "$dayName, $formattedDate"
    }

    fun formatLocalTime(time: LocalTime, format: String = "h:mm a"): String {
        return time.format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    }
}
