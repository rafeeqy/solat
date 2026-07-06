package my.naqibrafiqi.solat.domain.usecase

import my.naqibrafiqi.solat.domain.alarm.SolatAlarmScheduler
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class ScheduleNextPrayersUseCase @Inject constructor(
    private val alarmScheduler: SolatAlarmScheduler
) {

    operator fun invoke(dailyPrayers: List<DailyPrayer>, enabledPrayers: Set<String>) {
        val now = LocalDateTime.now()

        val upcomingPrayers = dailyPrayers.asSequence().flatMap { dailyPrayer ->
            dailyPrayer.prayerTimes
                .map { prayerTime ->
                    val dateTime = LocalDateTime.of(dailyPrayer.date, prayerTime.time)

                    Pair(prayerTime.prayer, dateTime)
                }
        }
            .filter { it.second.isAfter(now) }
            .sortedBy { (_, dateTime) -> dateTime }
            .groupBy { (prayer, _) -> prayer }
            .map { (_, prayerNamesAndDateTimes) -> prayerNamesAndDateTimes.first() }
            .toList()

        upcomingPrayers.forEach { (prayerName, dateTime) ->
            val solatAlarmItem = SolatAlarmItem(
                id = prayerName.ordinal,
                prayerName = prayerName,
                triggerAtMillis = dateTime.atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )

            if (enabledPrayers.contains(prayerName.name)) {
                alarmScheduler.schedule(solatAlarmItem)
            } else {
                alarmScheduler.cancel(solatAlarmItem)
            }
        }
    }
}