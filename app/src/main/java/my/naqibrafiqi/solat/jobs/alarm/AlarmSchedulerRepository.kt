package my.naqibrafiqi.solat.jobs.alarm

import my.naqibrafiqi.solat.ui.viewmodel.WaktuSolat

interface AlarmSchedulerRepository {
    fun schedule(
        zoneId: Int,
        zoneCode: String,
        nextPrayer: Long
    )

    fun cancel(prayer: WaktuSolat)
}