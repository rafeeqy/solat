package my.naqibrafiqi.solat.domain.usecase

import my.naqibrafiqi.solat.domain.alarm.SolatAlarmScheduler
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.util.SolatDateTime
import java.time.Clock
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Schedules one alarm per enabled prayer, at that prayer's next time in [dailyPrayers], and
 * cancels the alarm of every prayer that isn't enabled.
 *
 * Each prayer has a single alarm (its id is the prayer's ordinal), so scheduling replaces any
 * earlier alarm for the same prayer.
 */
class ScheduleNextPrayersUseCase @Inject constructor(
    private val alarmScheduler: SolatAlarmScheduler,
    private val clock: Clock
) {

    operator fun invoke(dailyPrayers: List<DailyPrayer>, enabledPrayers: Set<String>) {
        val now = LocalDateTime.now(clock)

        val nextTimes: Map<PrayerName, LocalDateTime> = dailyPrayers
            .flatMap { dailyPrayer ->
                dailyPrayer.prayerTimes.map { prayerTime ->
                    prayerTime.prayer to LocalDateTime.of(dailyPrayer.date, prayerTime.time)
                }
            }
            .filter { (_, dateTime) -> dateTime.isAfter(now) }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .mapValues { (_, dateTimes) -> dateTimes.min() }

        PrayerName.entries.forEach { prayer ->
            val nextTime = nextTimes[prayer]

            val item = SolatAlarmItem(
                id = prayer.ordinal,
                prayerName = prayer,
                triggerAtMillis = nextTime
                    ?.atZone(SolatDateTime.ZONE)
                    ?.toInstant()
                    ?.toEpochMilli()
                    ?: 0L
            )

            when {
                prayer.name !in enabledPrayers -> alarmScheduler.cancel(item)
                nextTime != null -> alarmScheduler.schedule(item)
                // Enabled, but no upcoming time is loaded (e.g. offline): keep whatever alarm it
                // has. The next refresh schedules it.
            }
        }
    }
}
