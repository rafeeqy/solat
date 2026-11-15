package my.naqibrafiqi.solat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import my.naqibrafiqi.solat.data.worker.DailyAlarmWorker
import javax.inject.Inject

/** Receives prayer alarms. The work is done in [PrayerAlarmHandler]. */
@AndroidEntryPoint
class SolatAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var prayerAlarmHandler: PrayerAlarmHandler

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TRIGGER_PRAYER_ALARM) {
            return
        }

        val pendingResult = goAsync()

        scope.launch {
            try {
                prayerAlarmHandler.handle(intent)
            } catch (e: Exception) {
                Log.w(TAG, "Couldn't handle the prayer alarm", e)
            } finally {
                // Schedule this prayer's next alarm (tomorrow's), and refresh the others.
                DailyAlarmWorker.enqueueNow(workManager)
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "SolatAlarmReceiver"

        const val ACTION_TRIGGER_PRAYER_ALARM = "my.naqibrafiqi.solat.ACTION_TRIGGER_PRAYER_ALARM"
        const val EXTRA_TRIGGER_AT_MILLIS = "EXTRA_TRIGGER_AT_MILLIS"

        /** The alarm id, which is also the prayer's ordinal. 1.0.x alarms set it too. */
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"

        /** The formatted time that 1.0.x alarms carry. */
        const val LEGACY_EXTRA_PRAYER_TIME = "EXTRA_PRAYER_TIME"

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
