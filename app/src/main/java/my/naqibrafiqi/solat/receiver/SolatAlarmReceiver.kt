package my.naqibrafiqi.solat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
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

        notificationManager.showNotification(
            notificationId = alarmId,
            title = "Waktu $prayerName ($prayerTime)",
            message = "Telah masuk waktu $prayerName ($prayerTime) bagi kawasan anda.",
            deepLinkUri = "solat://prayer/daily"
        )
    }
}
