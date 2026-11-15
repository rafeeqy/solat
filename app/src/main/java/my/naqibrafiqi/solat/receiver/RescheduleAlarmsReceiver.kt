package my.naqibrafiqi.solat.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import my.naqibrafiqi.solat.data.worker.DailyAlarmWorker
import javax.inject.Inject

/**
 * Schedules the prayer alarms again when they may have been lost or gone out of date:
 * - after a reboot, which clears every alarm;
 * - after an app update, so alarms set by an older version are replaced;
 * - after the clock is changed by hand;
 * - when exact alarms are allowed, so inexact alarms become exact.
 *
 * The work itself runs in [DailyAlarmWorker], so it isn't cut short when this receiver returns.
 */
@AndroidEntryPoint
class RescheduleAlarmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in HANDLED_ACTIONS) {
            DailyAlarmWorker.enqueueNow(workManager)
        }
    }

    private companion object {
        val HANDLED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
        )
    }
}
