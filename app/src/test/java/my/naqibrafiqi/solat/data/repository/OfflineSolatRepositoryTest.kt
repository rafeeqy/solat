package my.naqibrafiqi.solat.data.repository

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import my.naqibrafiqi.solat.data.local.db.SolatDatabase
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.testing.FakeWaktuSolatApi
import my.naqibrafiqi.solat.testing.TIMETABLE
import my.naqibrafiqi.solat.util.states.Resource
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.time.LocalDate
import java.time.YearMonth
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class OfflineSolatRepositoryTest {

    private lateinit var database: SolatDatabase
    private lateinit var dao: SolatDao
    private val api = FakeWaktuSolatApi()
    private lateinit var repository: OfflineSolatRepository
    private var zoneId = 0

    private val defaultTimeZone = TimeZone.getDefault()

    @Before
    fun setUp() = runTest {
        val context: Context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(context, SolatDatabase::class.java).build()
        dao = database.solatDao()
        repository = OfflineSolatRepository(api, dao)

        zoneId = dao.insertZones(
            listOf(ZoneEntity(code = "JHR02", state = "Johor", district = "-", createdDate = 0))
        ).single().toInt()
    }

    @After
    fun tearDown() {
        database.close()
        TimeZone.setDefault(defaultTimeZone)
    }

    private class Fetched(val emitted: List<Resource<*>>, val dailyPrayers: List<DailyPrayer>) {
        val dates get() = dailyPrayers.map { it.date }
        val hadError get() = emitted.any { it is Resource.Error }
    }

    /** Collects until the first successful result, keeping what came before it. */
    private suspend fun fetch(today: LocalDate): Fetched {
        val emitted = mutableListOf<Resource<*>>()

        val success = repository.fetchDailyPrayersForTodayAndTomorrow(zoneId, "JHR02", today)
            .onEach { emitted += it }
            .filterIsInstance<Resource.Success<List<DailyPrayer>>>()
            .first()

        return Fetched(emitted, success.data)
    }

    /** Fetches [month]'s prayers once, as the app would on an earlier day, then forgets the call. */
    private suspend fun alreadySaved(month: YearMonth) {
        fetch(month.atDay(15))
        api.requestedMonths.clear()
    }

    @Test
    fun `downloads this month when nothing is saved`() = runTest {
        val result = fetch(LocalDate.of(2026, 11, 15))

        assertEquals(listOf(YearMonth.of(2026, 11)), api.requestedMonths)
        assertEquals(listOf(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 16)), result.dates)
    }

    @Test
    fun `downloads nothing when both days are saved`() = runTest {
        alreadySaved(YearMonth.of(2026, 11))

        val result = fetch(LocalDate.of(2026, 11, 16))

        assertTrue(api.requestedMonths.isEmpty())
        assertEquals(2, result.dailyPrayers.size)
    }

    @Test
    fun `on the last day of a month, downloads next month`() = runTest {
        alreadySaved(YearMonth.of(2026, 11))

        val result = fetch(LocalDate.of(2026, 11, 30))

        // 1.0.1 downloaded nothing here, so 1 Dec's Subuh alarm was never set.
        assertEquals(listOf(YearMonth.of(2026, 12)), api.requestedMonths)
        assertEquals(listOf(LocalDate.of(2026, 11, 30), LocalDate.of(2026, 12, 1)), result.dates)
    }

    @Test
    fun `on the last day of the year with nothing saved, downloads both months`() = runTest {
        val result = fetch(LocalDate.of(2026, 12, 31))

        assertEquals(listOf(YearMonth.of(2026, 12), YearMonth.of(2027, 1)), api.requestedMonths)
        assertEquals(listOf(LocalDate.of(2026, 12, 31), LocalDate.of(2027, 1, 1)), result.dates)
    }

    @Test
    fun `offline, reports the error and still returns what's saved`() = runTest {
        alreadySaved(YearMonth.of(2026, 11))
        api.failWith = IOException("No internet")

        val result = fetch(LocalDate.of(2026, 11, 30))

        assertTrue(result.hadError)
        assertEquals(listOf(LocalDate.of(2026, 11, 30)), result.dates)
    }

    @Test
    fun `refresh downloads today's month again even when it's saved`() = runTest {
        alreadySaved(YearMonth.of(2026, 11))

        repository.refreshPrayerTimes(zoneId, "JHR02", LocalDate.of(2026, 11, 15))

        assertEquals(listOf(YearMonth.of(2026, 11)), api.requestedMonths)
        assertEquals(2, fetch(LocalDate.of(2026, 11, 15)).dailyPrayers.size)
    }

    @Test
    fun `refresh on the last day of a month downloads both months`() = runTest {
        repository.refreshPrayerTimes(zoneId, "JHR02", LocalDate.of(2026, 11, 30))

        assertEquals(listOf(YearMonth.of(2026, 11), YearMonth.of(2026, 12)), api.requestedMonths)
    }

    @Test
    fun `refresh fails when offline, keeping what's saved`() = runTest {
        alreadySaved(YearMonth.of(2026, 11))
        api.failWith = IOException("No internet")

        val failure = runCatching {
            repository.refreshPrayerTimes(zoneId, "JHR02", LocalDate.of(2026, 11, 15))
        }.exceptionOrNull()

        assertTrue(failure is IOException)
        api.failWith = null
        assertEquals(2, fetch(LocalDate.of(2026, 11, 15)).dailyPrayers.size)
        assertTrue(api.requestedMonths.isEmpty())
    }

    @Test
    fun `prayer times are Malaysia time whatever the phone's time zone`() = runTest {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        val today = fetch(LocalDate.of(2026, 11, 15)).dailyPrayers.first()

        assertEquals(
            TIMETABLE,
            today.prayerTimes.associate { it.prayer to it.time }
        )
        assertEquals(PrayerName.entries, today.prayerTimes.map { it.prayer })
    }
}
