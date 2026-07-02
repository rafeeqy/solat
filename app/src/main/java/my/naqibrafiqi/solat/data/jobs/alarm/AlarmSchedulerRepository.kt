package my.naqibrafiqi.solat.data.jobs.alarm

import my.naqibrafiqi.solat.ui.feature.prayer.WaktuSolat

interface AlarmSchedulerRepository {
    fun schedule(
        zoneId: Int,
        zoneCode: String,
        nextPrayer: Long
    )

    fun cancel(prayer: WaktuSolat)
}