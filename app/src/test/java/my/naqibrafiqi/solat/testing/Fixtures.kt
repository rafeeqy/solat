package my.naqibrafiqi.solat.testing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import my.naqibrafiqi.solat.data.local.datastore.DataStoreManager
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.remote.WaktuSolatApiService
import my.naqibrafiqi.solat.data.remote.model.PrayerDetailsDto
import my.naqibrafiqi.solat.data.remote.model.PrayerTimeDto
import my.naqibrafiqi.solat.data.remote.model.ZoneDto
import my.naqibrafiqi.solat.domain.alarm.SolatAlarmScheduler
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.PrayerTime
import my.naqibrafiqi.solat.domain.model.alarm.SolatAlarmItem
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.model.enums.ThemeSettings
import my.naqibrafiqi.solat.util.SolatDateTime
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.util.Locale

/** Malaysia time, the zone every prayer time is in. */
val MYT = SolatDateTime.ZONE

/** A clock stopped at [dateTime], Malaysia time. */
fun clockAt(dateTime: LocalDateTime): Clock = Clock.fixed(dateTime.atZone(MYT).toInstant(), MYT)

/** Epoch milliseconds of [time] on [date], Malaysia time. */
fun millisAt(date: LocalDate, time: LocalTime): Long =
    date.atTime(time).atZone(MYT).toInstant().toEpochMilli()

/** One day's timetable for JHR02 (15 Nov 2026). Tests use it for every day. */
val TIMETABLE: Map<PrayerName, LocalTime> = mapOf(
    PrayerName.IMSAK to LocalTime.of(5, 27),
    PrayerName.SUBUH to LocalTime.of(5, 37),
    PrayerName.SYURUK to LocalTime.of(6, 51),
    PrayerName.DHUHA to LocalTime.of(7, 16),
    PrayerName.ZOHOR to LocalTime.of(12, 52),
    PrayerName.ASAR to LocalTime.of(16, 14),
    PrayerName.MAGHRIB to LocalTime.of(18, 52),
    PrayerName.ISYAK to LocalTime.of(20, 5)
)

fun dailyPrayer(date: LocalDate): DailyPrayer =
    DailyPrayer(
        id = 1,
        zoneId = 1,
        date = date,
        hijri = "1448-05-05",
        prayerTimes = TIMETABLE.map { (prayer, time) ->
            PrayerTime(prayer = prayer, time = time, tableRowName = prayer.name.lowercase())
        }
    )

/** A month as the Waktu Solat API returns it: every day, with times as epoch seconds. */
fun monthDto(month: YearMonth, zone: String = "JHR02"): PrayerDetailsDto =
    PrayerDetailsDto(
        zone = zone,
        year = month.year,
        month = month.month.name.take(3),
        monthNumber = month.monthValue,
        lastUpdated = null,
        prayers = (1..month.lengthOfMonth()).map { day ->
            val date = month.atDay(day)
            fun at(prayer: PrayerName) = millisAt(date, TIMETABLE.getValue(prayer)) / 1000

            PrayerTimeDto(
                day = day,
                hijri = "1448-05-%02d".format(Locale.US, day),
                imsak = at(PrayerName.IMSAK),
                fajr = at(PrayerName.SUBUH),
                syuruk = at(PrayerName.SYURUK),
                dhuha = at(PrayerName.DHUHA),
                dhuhr = at(PrayerName.ZOHOR),
                asr = at(PrayerName.ASAR),
                maghrib = at(PrayerName.MAGHRIB),
                isha = at(PrayerName.ISYAK)
            )
        }
    )

/** Records the alarms scheduled and cancelled for each prayer. */
class FakeAlarmScheduler : SolatAlarmScheduler {
    val scheduled = mutableMapOf<PrayerName, Long>()
    val cancelled = mutableSetOf<PrayerName>()

    override fun schedule(item: SolatAlarmItem) {
        scheduled[item.prayerName] = item.triggerAtMillis
        cancelled -= item.prayerName
    }

    override fun cancel(item: SolatAlarmItem) {
        scheduled -= item.prayerName
        cancelled += item.prayerName
    }
}

class FakeSolatDataStore(
    zoneCode: String? = "JHR02",
    enabledPrayers: Set<String> = DataStoreManager.DEFAULT_ENABLED_PRAYERS
) : SolatDataStore {
    private val zoneCode = MutableStateFlow(zoneCode)
    private val enabledPrayers = MutableStateFlow(enabledPrayers)
    private val theme = MutableStateFlow(ThemeSettings.SYSTEM)

    override fun getTheme(): Flow<ThemeSettings> = theme

    override suspend fun setTheme(theme: ThemeSettings) {
        this.theme.value = theme
    }

    override fun getZoneCode(): Flow<String?> = zoneCode

    override suspend fun setZoneCode(code: String) {
        zoneCode.value = code
    }

    override fun getEnabledPrayers(): Flow<Set<String>> = enabledPrayers.map { it }

    override suspend fun togglePrayerNotification(prayerName: String, isEnabled: Boolean) {
        enabledPrayers.value =
            if (isEnabled) enabledPrayers.value + prayerName else enabledPrayers.value - prayerName
    }
}

/** Serves [monthDto] for any month, records each month asked for, and can fail like no network. */
class FakeWaktuSolatApi : WaktuSolatApiService {
    val requestedMonths = mutableListOf<YearMonth>()
    var failWith: Exception? = null

    override suspend fun getAllZones(): List<ZoneDto> =
        listOf(ZoneDto(jakimCode = "JHR02", negeri = "Johor", daerah = "Johor Bahru"))

    override suspend fun getPrayerTimes(zone: String, year: Int, month: Int): PrayerDetailsDto {
        failWith?.let { throw it }

        val yearMonth = YearMonth.of(year, month)
        requestedMonths += yearMonth

        return monthDto(yearMonth, zone)
    }
}
