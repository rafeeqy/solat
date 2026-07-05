package my.naqibrafiqi.solat.domain.alarm

import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem

interface SolatAlarmScheduler {

    fun schedule(item: SolatAlarmItem)

    fun cancel(item: SolatAlarmItem)
}
