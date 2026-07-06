package my.naqibrafiqi.solat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.util.SolatNotificationManager
import javax.inject.Inject

@AndroidEntryPoint
class SolatAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: SolatNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "my.naqibrafiqi.solat.ACTION_TRIGGER_PRAYER_ALARM") {
            return
        }

        val prayerName = intent.getStringExtra("EXTRA_PRAYER_NAME") ?: return
        val prayerTime = intent.getStringExtra("EXTRA_PRAYER_TIME") ?: "-:--"
        val alarmId = intent.getIntExtra("EXTRA_ALARM_ID", 0)

        val title =
            context.getString(R.string.prayer_notification_title, "$prayerName ($prayerTime)")
        val message =
            context.getString(R.string.prayer_notification_message, "$prayerName ($prayerTime)")

        notificationManager.showNotification(
            notificationId = alarmId,
            title = title,
            message = message,
            deepLinkUri = "solat://prayer/daily"
        )
    }
}
