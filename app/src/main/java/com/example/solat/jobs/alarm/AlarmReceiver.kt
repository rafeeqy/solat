package com.example.solat.jobs.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.solat.util.SolatDateTime
import com.example.solat.util.makePrayerNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmSchedulerRepository: AlarmSchedulerRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra(PrayerIntentExtraKeys.PRAYER_NAME.name)
        val prayerTime = intent?.getLongExtra(
            PrayerIntentExtraKeys.PRAYER_TIME.name,
            System.currentTimeMillis()
        )
        val zoneLocation = intent?.getStringExtra(PrayerIntentExtraKeys.ZONE_LOCATION.name)
        val zoneId = intent?.getIntExtra(PrayerIntentExtraKeys.ZONE_ID.name, 5) ?: 5
        val zoneCode = intent?.getStringExtra(PrayerIntentExtraKeys.ZONE_CODE.name) ?: "KDH01"

        val formattedPrayerTime = if (prayerTime != null) SolatDateTime.formatDate(
            date = prayerTime,
            format = "hh:mm a"
        ) else "UNKNOWN_PRAYER_TIME"

        makePrayerNotification(
            title = "Peringatan $prayerName!",
            message = "Telah masuk waktu $prayerName ($formattedPrayerTime) bagi kawasan $zoneLocation",
            context = context!!
        )

        alarmSchedulerRepository.schedule(
            zoneId,
            zoneCode,
            prayerTime ?: System.currentTimeMillis()
        )
    }
}