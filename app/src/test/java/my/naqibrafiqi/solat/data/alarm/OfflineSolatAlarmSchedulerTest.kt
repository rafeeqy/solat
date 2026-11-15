package my.naqibrafiqi.solat.data.alarm

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.receiver.SolatAlarmReceiver
import my.naqibrafiqi.solat.testing.TIMETABLE
import my.naqibrafiqi.solat.testing.millisAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.LocalDate

/** Runs the real scheduler against Robolectric's AlarmManager. */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class OfflineSolatAlarmSchedulerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val scheduler = OfflineSolatAlarmScheduler(context, alarmManager)

    private val today = LocalDate.of(2026, 11, 15)

    private fun alarm(prayer: PrayerName, date: LocalDate = today) =
        SolatAlarmItem(
            id = prayer.ordinal,
            prayerName = prayer,
            triggerAtMillis = millisAt(date, TIMETABLE.getValue(prayer))
        )

    private fun scheduledAlarms() = shadowOf(alarmManager).scheduledAlarms

    @Test
    fun `cancel removes the alarm that schedule set`() {
        scheduler.schedule(alarm(PrayerName.ASAR))
        assertEquals(1, scheduledAlarms().size)

        // 1.0.1 built a different intent here, so the alarm stayed scheduled.
        scheduler.cancel(alarm(PrayerName.ASAR))

        assertTrue(scheduledAlarms().isEmpty())
    }

    @Test
    fun `cancelling one prayer keeps the others`() {
        scheduler.schedule(alarm(PrayerName.ASAR))
        scheduler.schedule(alarm(PrayerName.MAGHRIB))

        scheduler.cancel(alarm(PrayerName.ASAR))

        assertEquals(
            listOf(alarm(PrayerName.MAGHRIB).triggerAtMillis),
            scheduledAlarms().map { it.triggerAtMs }
        )
    }

    @Test
    fun `scheduling a prayer again replaces its alarm`() {
        scheduler.schedule(alarm(PrayerName.ASAR, today))
        scheduler.schedule(alarm(PrayerName.ASAR, today.plusDays(1)))

        assertEquals(
            listOf(alarm(PrayerName.ASAR, today.plusDays(1)).triggerAtMillis),
            scheduledAlarms().map { it.triggerAtMs }
        )
    }

    @Test
    fun `cancelling a prayer with no alarm does nothing`() {
        scheduler.schedule(alarm(PrayerName.MAGHRIB))

        scheduler.cancel(alarm(PrayerName.ASAR))

        assertEquals(1, scheduledAlarms().size)
    }

    @Test
    fun `the alarm tells the receiver which prayer it is and when`() {
        val maghrib = alarm(PrayerName.MAGHRIB)
        scheduler.schedule(maghrib)

        val intent = shadowOf(scheduledAlarms().single().operation).savedIntent

        assertEquals(SolatAlarmReceiver.ACTION_TRIGGER_PRAYER_ALARM, intent.action)
        assertEquals(
            PrayerName.MAGHRIB.ordinal,
            intent.getIntExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, -1)
        )
        assertEquals(
            maghrib.triggerAtMillis,
            intent.getLongExtra(SolatAlarmReceiver.EXTRA_TRIGGER_AT_MILLIS, -1L)
        )
    }
}
