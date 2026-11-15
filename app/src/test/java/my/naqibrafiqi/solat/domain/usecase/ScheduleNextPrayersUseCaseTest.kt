package my.naqibrafiqi.solat.domain.usecase

import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.testing.FakeAlarmScheduler
import my.naqibrafiqi.solat.testing.TIMETABLE
import my.naqibrafiqi.solat.testing.clockAt
import my.naqibrafiqi.solat.testing.dailyPrayer
import my.naqibrafiqi.solat.testing.millisAt
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.TimeZone

class ScheduleNextPrayersUseCaseTest {

    private val scheduler = FakeAlarmScheduler()

    private val today = LocalDate.of(2026, 11, 15)
    private val tomorrow = today.plusDays(1)
    private val todayAndTomorrow = listOf(dailyPrayer(today), dailyPrayer(tomorrow))

    private val defaultTimeZone = TimeZone.getDefault()

    @After
    fun restoreTimeZone() {
        TimeZone.setDefault(defaultTimeZone)
    }

    private fun scheduleAt(
        now: LocalDateTime,
        enabled: Set<String> = DataStoreManager.DEFAULT_ENABLED_PRAYERS,
        dailyPrayers: List<DailyPrayer> = todayAndTomorrow
    ) = ScheduleNextPrayersUseCase(scheduler, clockAt(now))(dailyPrayers, enabled)

    private fun at(date: LocalDate, prayer: PrayerName) = millisAt(date, TIMETABLE.getValue(prayer))

    @Test
    fun `schedules each enabled prayer at its next time`() {
        scheduleAt(today.atTime(16, 26))

        assertEquals(
            mapOf(
                PrayerName.MAGHRIB to at(today, PrayerName.MAGHRIB),
                PrayerName.ISYAK to at(today, PrayerName.ISYAK),
                PrayerName.SUBUH to at(tomorrow, PrayerName.SUBUH),
                PrayerName.ZOHOR to at(tomorrow, PrayerName.ZOHOR),
                PrayerName.ASAR to at(tomorrow, PrayerName.ASAR)
            ),
            scheduler.scheduled
        )
    }

    @Test
    fun `cancels every prayer that is switched off`() {
        scheduleAt(
            today.atTime(16, 26),
            enabled = setOf(PrayerName.SUBUH.name, PrayerName.MAGHRIB.name)
        )

        assertEquals(setOf(PrayerName.SUBUH, PrayerName.MAGHRIB), scheduler.scheduled.keys)
        assertEquals(
            PrayerName.entries.toSet() - PrayerName.SUBUH - PrayerName.MAGHRIB,
            scheduler.cancelled
        )
    }

    @Test
    fun `cancels switched-off prayers even when no prayer times are loaded`() {
        scheduleAt(
            today.atTime(16, 26),
            enabled = setOf(PrayerName.SUBUH.name),
            dailyPrayers = emptyList()
        )

        assertTrue(scheduler.scheduled.isEmpty())
        assertEquals(PrayerName.entries.toSet() - PrayerName.SUBUH, scheduler.cancelled)
    }

    @Test
    fun `alarms are set in Malaysia time whatever the phone's time zone`() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        scheduleAt(today.atTime(16, 26))

        // 6:52 pm in Malaysia is 10:52 GMT. Phase 1 fixed alarms that went off at 6:52 pm GMT.
        assertEquals(
            Instant.parse("2026-11-15T10:52:00Z").toEpochMilli(),
            scheduler.scheduled[PrayerName.MAGHRIB]
        )
    }

    @Test
    fun `a prayer whose time is now is scheduled for the next day`() {
        scheduleAt(today.atTime(TIMETABLE.getValue(PrayerName.MAGHRIB)))

        assertEquals(at(tomorrow, PrayerName.MAGHRIB), scheduler.scheduled[PrayerName.MAGHRIB])
    }

    @Test
    fun `after the last prayer of the day, tomorrow's prayers are scheduled`() {
        scheduleAt(today.atTime(21, 0))

        assertEquals(
            DataStoreManager.DEFAULT_ENABLED_PRAYERS.associate { name ->
                PrayerName.valueOf(name) to at(tomorrow, PrayerName.valueOf(name))
            },
            scheduler.scheduled
        )
    }
}
