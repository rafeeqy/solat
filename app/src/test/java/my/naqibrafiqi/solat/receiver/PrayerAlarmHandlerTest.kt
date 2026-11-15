package my.naqibrafiqi.solat.receiver

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.testing.FakeSolatDataStore
import my.naqibrafiqi.solat.testing.TIMETABLE
import my.naqibrafiqi.solat.testing.millisAt
import my.naqibrafiqi.solat.util.SolatNotificationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PrayerAlarmHandlerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val today = LocalDate.of(2026, 11, 15)

    @Before
    fun grantNotificationPermission() {
        shadowOf(context as Application).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun handler(enabledPrayers: Set<String> = DataStoreManager.DEFAULT_ENABLED_PRAYERS) =
        PrayerAlarmHandler(
            context = context,
            dataStore = FakeSolatDataStore(enabledPrayers = enabledPrayers),
            notificationManager = SolatNotificationManager(context, notificationManager)
        )

    private fun alarmIntent(prayer: PrayerName) =
        Intent(SolatAlarmReceiver.ACTION_TRIGGER_PRAYER_ALARM)
            .putExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, prayer.ordinal)
            .putExtra(
                SolatAlarmReceiver.EXTRA_TRIGGER_AT_MILLIS,
                millisAt(today, TIMETABLE.getValue(prayer))
            )

    private fun postedTitles() = shadowOf(notificationManager).allNotifications.map {
        it.extras.getCharSequence(Notification.EXTRA_TITLE).toString()
    }

    @Test
    fun `posts the notification of an enabled prayer, with its Malaysia time`() = runTest {
        val notified = handler().handle(alarmIntent(PrayerName.MAGHRIB))

        assertEquals(PrayerName.MAGHRIB, notified)

        val maghrib = context.getString(PrayerName.MAGHRIB.resId)
        assertEquals(listOf("$maghrib (6:52 pm) Prayer"), postedTitles())
    }

    @Test
    fun `a prayer switched off after its alarm was set doesn't notify`() = runTest {
        val enabledWithoutAsar = DataStoreManager.DEFAULT_ENABLED_PRAYERS - PrayerName.ASAR.name

        val notified = handler(enabledWithoutAsar).handle(alarmIntent(PrayerName.ASAR))

        assertNull(notified)
        assertTrue(postedTitles().isEmpty())
    }

    @Test
    fun `alarms set by 1_0_x show the time they carry`() = runTest {
        val legacyIntent = Intent(SolatAlarmReceiver.ACTION_TRIGGER_PRAYER_ALARM)
            .putExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, PrayerName.ISYAK.ordinal)
            .putExtra(SolatAlarmReceiver.LEGACY_EXTRA_PRAYER_TIME, "8:05 pm")

        handler().handle(legacyIntent)

        val isyak = context.getString(PrayerName.ISYAK.resId)
        assertEquals(listOf("$isyak (8:05 pm) Prayer"), postedTitles())
    }

    @Test
    fun `ignores other intents and unknown alarm ids`() = runTest {
        val otherAction = Intent("something.else")
            .putExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, PrayerName.SUBUH.ordinal)
        val unknownId = Intent(SolatAlarmReceiver.ACTION_TRIGGER_PRAYER_ALARM)
            .putExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, 99)

        assertNull(handler().handle(otherAction))
        assertNull(handler().handle(unknownId))
        assertTrue(postedTitles().isEmpty())
    }
}
