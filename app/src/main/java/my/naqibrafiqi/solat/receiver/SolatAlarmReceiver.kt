package my.naqibrafiqi.solat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.worker.DailyAlarmWorker
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.util.SolatDateTime
import my.naqibrafiqi.solat.util.SolatNotificationManager
import my.naqibrafiqi.solat.util.converter.toJawiNumerals
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class SolatAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: SolatNotificationManager

    @Inject
    lateinit var dataStore: SolatDataStore

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TRIGGER_PRAYER_ALARM) {
            return
        }

        // The alarm id is the prayer's ordinal (see ScheduleNextPrayersUseCase).
        val prayer = PrayerName.entries.getOrNull(intent.getIntExtra(EXTRA_ALARM_ID, -1)) ?: return

        val pendingResult = goAsync()

        scope.launch {
            try {
                val enabledPrayers = dataStore.getEnabledPrayers().firstOrNull()
                    ?: DataStoreManager.DEFAULT_ENABLED_PRAYERS

                // A prayer switched off after its alarm was set must not notify.
                if (prayer.name in enabledPrayers) {
                    showNotification(context, intent, prayer)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Couldn't show the $prayer notification", e)
            } finally {
                // Schedule this prayer's next alarm (tomorrow's), and refresh the others.
                DailyAlarmWorker.enqueueNow(workManager)
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, intent: Intent, prayer: PrayerName) {
        val prayerName = context.getString(prayer.resId)
        val prayerTime = formatPrayerTime(context, intent)
        val label = "$prayerName ($prayerTime)"

        notificationManager.showNotification(
            notificationId = prayer.ordinal,
            title = context.getString(R.string.prayer_notification_title, label),
            message = context.getString(R.string.prayer_notification_message, label),
            deepLinkUri = "solat://prayer/daily"
        )
    }

    private fun formatPrayerTime(context: Context, intent: Intent): String {
        val triggerAtMillis = intent.getLongExtra(EXTRA_TRIGGER_AT_MILLIS, -1L)

        if (triggerAtMillis < 0) {
            // Alarms set by 1.0.x carry the time already formatted.
            return intent.getStringExtra(LEGACY_EXTRA_PRAYER_TIME) ?: "-:--"
        }

        val time = SolatDateTime.formatLocalTime(
            time = Instant.ofEpochMilli(triggerAtMillis).atZone(SolatDateTime.ZONE).toLocalTime()
        ).lowercase()

        val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

        return if (isRtl) time.toJawiNumerals() else time
    }

    companion object {
        private const val TAG = "SolatAlarmReceiver"

        const val ACTION_TRIGGER_PRAYER_ALARM = "my.naqibrafiqi.solat.ACTION_TRIGGER_PRAYER_ALARM"
        const val EXTRA_TRIGGER_AT_MILLIS = "EXTRA_TRIGGER_AT_MILLIS"

        /** The alarm id, which is also the prayer's ordinal. 1.0.x alarms set it too. */
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        private const val LEGACY_EXTRA_PRAYER_TIME = "EXTRA_PRAYER_TIME"

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
