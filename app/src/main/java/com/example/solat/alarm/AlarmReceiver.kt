package com.example.solat.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.solat.util.makePrayerNotification

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "Prayer"

        makePrayerNotification(
            title = "Masuk Waktu Solat $prayerName",
            message = "Sila tunaikan solat...",
            context = context!!
        )
    }
}