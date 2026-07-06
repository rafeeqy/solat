package my.naqibrafiqi.solat.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import my.naqibrafiqi.solat.domain.alarm.SolatAlarmScheduler
import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem
import my.naqibrafiqi.solat.receiver.SolatAlarmReceiver
import my.naqibrafiqi.solat.util.converter.toJawiNumerals
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class OfflineSolatAlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : SolatAlarmScheduler {

    override fun schedule(item: SolatAlarmItem) {
        val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

        val formattedPrayerTime = Instant.ofEpochMilli(item.triggerAtMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
            .lowercase()

        val prayerName = context.getString(item.prayerName.resId)
        val prayerTime = if (isRtl) formattedPrayerTime.toJawiNumerals() else formattedPrayerTime

        val intent =
            Intent(context, SolatAlarmReceiver::class.java).apply {
                action = "my.naqibrafiqi.solat.ACTION_TRIGGER_PRAYER_ALARM"
                putExtra("EXTRA_PRAYER_NAME", prayerName)
                putExtra("EXTRA_PRAYER_TIME", prayerTime)
                putExtra("EXTRA_ALARM_ID", item.id)
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                item.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ (API 31) (alarm permission is required and denied by default)
                if (alarmManager.canScheduleExactAlarms()) {
                    // Permission allowed, use exact alarm (more accurate)
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        item.triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    // If permission denied, use inexact alarm (less accurate)
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        item.triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6 to 11 (no explicit alarm permission)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.triggerAtMillis,
                    pendingIntent
                )
            } else {
                // Below Android 6 (no need alarm permission)
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    item.triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun cancel(item: SolatAlarmItem) {
        val intent = Intent(context, SolatAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

}
