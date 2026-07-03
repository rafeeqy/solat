package my.naqibrafiqi.solat.data.repository

import android.util.Log
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import my.naqibrafiqi.solat.data.local.datastore.SolatDataStore
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import my.naqibrafiqi.solat.data.local.db.entity.PrayerDetailsEntity
import my.naqibrafiqi.solat.data.local.db.entity.PrayerTimeEntity
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.local.db.relationship.DailyPrayerSummary
import my.naqibrafiqi.solat.data.mapper.toDomain
import my.naqibrafiqi.solat.data.mapper.toEntity
import my.naqibrafiqi.solat.data.remote.WaktuSolatApiService
import my.naqibrafiqi.solat.data.remote.model.ZoneDto
import my.naqibrafiqi.solat.domain.model.Zone
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import my.naqibrafiqi.solat.util.NextDayStatus
import my.naqibrafiqi.solat.util.SolatDateTime
import my.naqibrafiqi.solat.util.states.Resource
import javax.inject.Inject

class OfflineSolatRepository @Inject constructor(
    private val waktuSolatApiService: WaktuSolatApiService,
    private val solatDao: SolatDao,
    private val dataStore: SolatDataStore,
    private val workManager: WorkManager
) : SolatRepository {

    override fun fetchAllZones(): Flow<Resource<List<Zone>>> = flow {
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
                emit(Resource.Error(e))
            }
        }

        // Fetch zones with subscription (Flow)
        // - zones to be emitted, whether the new zones are fetched or not
        val zonesFlow = solatDao.loadAllZones()
            .map { zones -> zones.map(ZoneEntity::toDomain) }
            .map { zones -> Resource.Success(zones) }

        emitAll(zonesFlow)
    }

    override suspend fun remoteFetchAllZones(): List<Long> {
        val zones = waktuSolatApiService.getAllZones()

        return solatDao.insertZones(
            zones.map { zone ->
                ZoneEntity(
                    code = zone.jakimCode,
                    state = zone.negeri,
                    district = zone.daerah,
                    createdDate = SolatDateTime.nowEpochMilli()
                )
            }
        )
    }

    override suspend fun remoteFetchPrayerTimes(
        zoneId: Int,
        zoneCode: String,
        year: Int,
        month: Int
    ): Map<Long, List<Long>> {
        val prayer = waktuSolatApiService.getPrayerTimes(zoneCode, year, month)

        return solatDao.insertPrayerDetailsAndTimes(
            prayerDetailsEntity = PrayerDetailsEntity(
                zoneId = zoneId,
                year = prayer.year,
                month = prayer.month,
                monthNumber = prayer.monthNumber,
                apiLastUpdated = prayer.lastUpdated,
                createdDate = SolatDateTime.nowEpochMilli()
            ),
            prayerTimeEntities = prayer.prayers.map { time ->
                PrayerTimeEntity(
                    prayerDetailsId = 0,
                    day = time.day,
                    hijri = time.hijri,
                    fajr = time.fajr,
                    syuruk = time.syuruk,
                    dhuhr = time.dhuhr,
                    asr = time.asr,
                    maghrib = time.maghrib,
                    isha = time.isha,
                    createdDate = SolatDateTime.nowEpochMilli()
                )
            }
        )
    }

    override fun fetchZoneById(zoneId: Int): Flow<ZoneEntity?> {
        return if (zoneId > 0) solatDao.loadZoneById(zoneId) else flowOf(null)
    }

    override fun fetchDailyPrayers(
        zoneEntity: ZoneEntity
    ): Flow<List<Prayer>> = flow {
        val today = SolatDateTime.splitDate(SolatDateTime.nowEpochMilli())
        val tomorrow = SolatDateTime.splitDate(SolatDateTime.tomorrowEpochMilli())
        val tomorrowStatus = SolatDateTime.nextDayStatus()

        if (
            tomorrowStatus != NextDayStatus.NO_CHANGES &&
            solatDao.hasPrayerOnYearAndMonth(tomorrow.year, tomorrow.month) == null
        ) {
            try {
                remoteFetchPrayerTimes(
                    zoneEntity.zoneId,
                    zoneEntity.code,
                    tomorrow.year,
                    tomorrow.month
                )
            } catch (e: Exception) {
                Log.e("FETCHDAILYPRAYERS", "Error: ${e.message}")
            }
        }

        emitAll(
            solatDao.loadDailyPrayers(
                zoneEntity.zoneId,
                todayYear = today.year,
                tomorrowYear = tomorrow.year,
                todayMonth = today.month,
                tomorrowMonth = tomorrow.month,
                today = today.day,
                tomorrow = tomorrow.day,
            ).map { prayerDetails ->
                if (prayerDetails.isNotEmpty()) {
                    prayerDetails.map {
                        Prayer(
                            zoneId = zoneEntity.zoneId,
                            zoneCode = zoneEntity.code,
                            year = it.year,
                            month = it.month,
                            day = it.day,
                            prayers = parsePrayerTimes(it)
                        )
                    }
                } else {
                    emptyList()
                }
            }
        )
    }.flowOn(context = Dispatchers.IO)

    override fun fetchCurrentPrayer(
        zoneId: Int,
        zoneCode: String,
        date: Long
    ): Prayer? {
        val today = SolatDateTime.splitDate(date)

        val dailyPrayer = solatDao.loadPrayer(today.year, today.month, today.day)
        val prayer = if (dailyPrayer != null) {
            Prayer(
                zoneId = zoneId,
                zoneCode = zoneCode,
                year = dailyPrayer.year,
                month = dailyPrayer.month,
                day = dailyPrayer.day,
                prayers = parsePrayerTimes(dailyPrayer)
            )
        } else {
            null
        }

        return prayer
    }

    override suspend fun deletePrayerByZone(zoneId: Int) =
        solatDao.deletePrayerDetailsByZoneId(zoneId)

    private fun parsePrayerTimes(prayers: DailyPrayerSummary): Map<PrayerNames, Long> {
        return mapOf(
            PrayerNames.FAJR to (prayers.fajr.toString() + "000").toLong(),
            PrayerNames.DHUHR to (prayers.dhuhr.toString() + "000").toLong(),
            PrayerNames.ASR to (prayers.asr.toString() + "000").toLong(),
            PrayerNames.MAGHRIB to (prayers.maghrib.toString() + "000").toLong(),
            PrayerNames.ISHA to (prayers.isha.toString() + "000").toLong()
        )
    }
}

data class Prayer(
    val zoneId: Int,
    val zoneCode: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val prayers: Map<PrayerNames, Long>
)

enum class PrayerNames(val label: String) {
    FAJR("Subuh"),
    DHUHR("Zohor"),
    ASR("Asar"),
    MAGHRIB("Maghrib"),
    ISHA("Isyak")
}
