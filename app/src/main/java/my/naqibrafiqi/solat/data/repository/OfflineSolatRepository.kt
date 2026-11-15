package my.naqibrafiqi.solat.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.mapper.toDomain
import my.naqibrafiqi.solat.data.mapper.toEntity
import my.naqibrafiqi.solat.data.remote.WaktuSolatApiService
import my.naqibrafiqi.solat.data.remote.model.ZoneDto
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.PrayerTime
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.util.SolatDateTime
import my.naqibrafiqi.solat.util.states.Resource
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class OfflineSolatRepository @Inject constructor(
    private val waktuSolatApiService: WaktuSolatApiService,
    private val solatDao: SolatDao
) : SolatRepository {

    override fun fetchAllZones(): Flow<Resource<List<Zone>>> =
        flow {
            emit(Resource.Loading)

            // Fetch zones without subscription (with `first`)
            // - just to check if the local zones is empty
            val localZones = solatDao.loadAllZones().first()

            if (localZones.isEmpty()) {
                try {
                    // Fetch new zones from remote API and save to Room
                    val remoteZones = waktuSolatApiService.getAllZones()
                    val newZoneEntities = remoteZones.map(ZoneDto::toEntity)

                    solatDao.insertZones(zoneEntities = newZoneEntities)
                } catch (e: Exception) {
                    emit(Resource.Error(exception = e))
                }
            }

            // Fetch zones with subscription (Flow)
            // - zones to be emitted, whether the new zones are fetched or not
            val zonesFlow = solatDao.loadAllZones()
                .map { zones -> zones.map(ZoneEntity::toDomain) }
                .map { zones -> Resource.Success(data = zones) }

            emitAll(zonesFlow)

        }.flowOn(context = Dispatchers.IO)

    override fun fetchZoneByCode(zoneCode: String): Flow<Zone?> =
        solatDao.loadZoneByCode(zoneCode)
            .map { zone -> zone?.toDomain() }

    override fun fetchDailyPrayersForTodayAndTomorrow(
        zoneId: Int,
        zoneCode: String,
        today: LocalDate
    ): Flow<Resource<List<DailyPrayer>>> =
        flow {
            emit(Resource.Loading)

            val tomorrow = today.plusDays(1)

            val localPrayers = loadTodayAndTomorrow(zoneId, today, tomorrow).first()

            val savedDates = localPrayers.map { (details, time) ->
                LocalDate.of(details.year, details.monthNumber, time.day)
            }

            // Download the month of each day that isn't saved. On the last day of a month that's
            // usually just next month's times.
            val missingMonths = listOf(today, tomorrow)
                .filterNot { it in savedDates }
                .map { YearMonth.from(it) }
                .distinct()

            try {
                missingMonths.forEach { month ->
                    val remoteMonthlyPrayers = waktuSolatApiService.getPrayerTimes(
                        zoneCode,
                        month.year,
                        month.monthValue
                    )

                    solatDao.insertMonthlyPrayers(
                        prayerDetailsEntity = remoteMonthlyPrayers.toEntity(zoneId),
                        prayerTimeEntities = remoteMonthlyPrayers.prayers.map { prayer ->
                            prayer.toEntity(prayerDetailsId = 0)
                        }
                    )
                }
            } catch (e: Exception) {
                emit(Resource.Error(exception = e))
            }

            val prayersFlow =
                loadTodayAndTomorrow(zoneId, today, tomorrow).map { prayers ->
                    prayers.map { (details, time) ->
                        DailyPrayer(
                            id = details.prayerDetailsId,
                            zoneId = details.zoneId,
                            date = LocalDate.of(
                                details.year,
                                details.monthNumber,
                                time.day
                            ),
                            hijri = time.hijri,
                            prayerTimes = listOf(
                                PrayerTime(
                                    prayer = PrayerName.IMSAK,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.imsak),
                                    tableRowName = "imsak"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.SUBUH,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.fajr),
                                    tableRowName = "fajr"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.SYURUK,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.syuruk),
                                    tableRowName = "syuruk"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.DHUHA,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.dhuha),
                                    tableRowName = "dhuha"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.ZOHOR,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.dhuhr),
                                    tableRowName = "dhuhr"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.ASAR,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.asr),
                                    tableRowName = "asr"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.MAGHRIB,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.maghrib),
                                    tableRowName = "maghrib"
                                ),
                                PrayerTime(
                                    prayer = PrayerName.ISYAK,
                                    time = SolatDateTime.fromEpochSecondToLocalTime(time.isha),
                                    tableRowName = "isha"
                                )
                            )
                        )
                    }
                }.map { prayers -> Resource.Success(data = prayers) }

            emitAll(prayersFlow)

        }.flowOn(Dispatchers.IO)

    private fun loadTodayAndTomorrow(zoneId: Int, today: LocalDate, tomorrow: LocalDate) =
        solatDao.loadPrayerForTodayAndTomorrow(
            zoneId = zoneId,
            todayYear = today.year,
            todayMonth = today.monthValue,
            todayDay = today.dayOfMonth,
            tomorrowYear = tomorrow.year,
            tomorrowMonth = tomorrow.monthValue,
            tomorrowDay = tomorrow.dayOfMonth
        )
}
