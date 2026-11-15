package my.naqibrafiqi.solat.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import my.naqibrafiqi.solat.domain.alarm.SolatAlarmScheduler
import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem
import my.naqibrafiqi.solat.receiver.SolatAlarmReceiver
import javax.inject.Inject

class OfflineSolatAlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : SolatAlarmScheduler {

    override fun schedule(item: SolatAlarmItem) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            alarmIntent()
                .putExtra(SolatAlarmReceiver.EXTRA_ALARM_ID, item.id)
                .putExtra(SolatAlarmReceiver.EXTRA_TRIGGER_AT_MILLIS, item.triggerAtMillis),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val canScheduleExact =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.triggerAtMillis,
                    pendingIntent
                )
            } else {
                // Exact alarms aren't allowed (Android 12+ denies them by default): the alarm may
                // go off a few minutes late. It's scheduled again as exact once they're allowed.
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Couldn't schedule the ${item.prayerName} alarm", e)
        }
    }

    override fun cancel(item: SolatAlarmItem) {
        // Must match the intent schedule() used (same action and component; extras are ignored),
        // or FLAG_NO_CREATE finds nothing and the alarm stays scheduled.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            alarmIntent(),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun alarmIntent(): Intent =
        Intent(context, SolatAlarmReceiver::class.java)
            .setAction(SolatAlarmReceiver.ACTION_TRIGGER_PRAYER_ALARM)

    private companion object {
        const val TAG = "SolatAlarmScheduler"
    }
}
