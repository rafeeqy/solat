package my.naqibrafiqi.solat.receiver

import android.content.Context
import android.content.Intent
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.util.SolatDateTime
import my.naqibrafiqi.solat.util.SolatNotificationManager
import my.naqibrafiqi.solat.util.converter.toJawiNumerals
import java.time.Instant
import javax.inject.Inject

/**
 * What happens when a prayer alarm goes off: post the prayer's notification, unless the prayer
 * has been switched off since the alarm was set. [SolatAlarmReceiver] calls this.
 */
class PrayerAlarmHandler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dataStore: SolatDataStore,
    private val notificationManager: SolatNotificationManager
) {

    /** Returns the prayer that was notified, or null if nothing was posted. */
    suspend fun handle(intent: Intent): PrayerName? {
        if (intent.action != SolatAlarmReceiver.ACTION_TRIGGER_PRAYER_ALARM) {
            return null
        }

        // The alarm id is the prayer's ordinal (see ScheduleNextPrayersUseCase).
        val prayer = PrayerName.entries
            .getOrNull(intent.getIntExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, -1))
            ?: return null

        val enabledPrayers = dataStore.getEnabledPrayers().firstOrNull()
            ?: DataStoreManager.DEFAULT_ENABLED_PRAYERS

        // A prayer switched off after its alarm was set must not notify.
        if (prayer.name !in enabledPrayers) {
            return null
        }

        val label = "${context.getString(prayer.resId)} (${formatPrayerTime(intent)})"

        notificationManager.showNotification(
            notificationId = prayer.ordinal,
            title = context.getString(R.string.prayer_notification_title, label),
            message = context.getString(R.string.prayer_notification_message, label),
            deepLinkUri = "solat://prayer/daily"
        )

        return prayer
    }

    private fun formatPrayerTime(intent: Intent): String {
        val triggerAtMillis = intent.getLongExtra(SolatAlarmReceiver.EXTRA_TRIGGER_AT_MILLIS, -1L)

        if (triggerAtMillis < 0) {
            // Alarms set by 1.0.x carry the time already formatted.
            return intent.getStringExtra(SolatAlarmReceiver.LEGACY_EXTRA_PRAYER_TIME) ?: "-:--"
        }

        val time = SolatDateTime.formatLocalTime(
            time = Instant.ofEpochMilli(triggerAtMillis).atZone(SolatDateTime.ZONE).toLocalTime()
        ).lowercase()

        val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

        return if (isRtl) time.toJawiNumerals() else time
    }
}
