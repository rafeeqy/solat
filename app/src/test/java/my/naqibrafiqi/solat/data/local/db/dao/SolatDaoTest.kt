package my.naqibrafiqi.solat.data.local.db.dao

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import my.naqibrafiqi.solat.data.local.db.SolatDatabase
import my.naqibrafiqi.solat.data.local.db.entity.ZoneEntity
import my.naqibrafiqi.solat.data.mapper.toEntity
import my.naqibrafiqi.solat.testing.monthDto
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.YearMonth

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class SolatDaoTest {

    private lateinit var database: SolatDatabase
    private lateinit var dao: SolatDao
    private var zoneId = 0

    @Before
    fun createDatabase() = runTest {
        val context: Context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(context, SolatDatabase::class.java).build()
        dao = database.solatDao()
        zoneId = insertZone("JHR02")
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    private suspend fun insertZone(code: String): Int =
        dao.insertZones(
            listOf(ZoneEntity(code = code, state = "Johor", district = "-", createdDate = 0))
        ).single().toInt()

    private suspend fun saveMonth(month: YearMonth, zone: Int = zoneId) {
        val dto = monthDto(month)

        dao.insertMonthlyPrayers(
            prayerDetailsEntity = dto.toEntity(zone),
            prayerTimeEntities = dto.prayers.map { it.toEntity(prayerDetailsId = 0) }
        )
    }

    /** The dates the "today and tomorrow" query returns for [today]. */
    private suspend fun loadDates(today: LocalDate, zone: Int = zoneId): List<LocalDate> {
        val tomorrow = today.plusDays(1)

        return dao.loadPrayerForTodayAndTomorrow(
            zoneId = zone,
            todayYear = today.year,
            todayMonth = today.monthValue,
            todayDay = today.dayOfMonth,
            tomorrowYear = tomorrow.year,
            tomorrowMonth = tomorrow.monthValue,
            tomorrowDay = tomorrow.dayOfMonth
        ).first().map { (details, time) -> LocalDate.of(details.year, details.monthNumber, time.day) }
    }

    @Test
    fun `returns today and tomorrow`() = runTest {
        saveMonth(YearMonth.of(2026, 11))

        assertEquals(
            listOf(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 16)),
            loadDates(LocalDate.of(2026, 11, 15))
        )
    }

    @Test
    fun `on the last day of a month, the 1st of the same month isn't taken for tomorrow`() = runTest {
        saveMonth(YearMonth.of(2026, 11))

        // 1.0.1 returned 1 Nov and 30 Nov here, so December was never downloaded.
        assertEquals(listOf(LocalDate.of(2026, 11, 30)), loadDates(LocalDate.of(2026, 11, 30)))
    }

    @Test
    fun `on the last day of a month, tomorrow comes from the next month`() = runTest {
        saveMonth(YearMonth.of(2026, 11))
        saveMonth(YearMonth.of(2026, 12))

        assertEquals(
            listOf(LocalDate.of(2026, 11, 30), LocalDate.of(2026, 12, 1)),
            loadDates(LocalDate.of(2026, 11, 30))
        )
    }

    @Test
    fun `on the last day of the year, tomorrow comes from next year`() = runTest {
        saveMonth(YearMonth.of(2026, 1))
        saveMonth(YearMonth.of(2026, 12))
        saveMonth(YearMonth.of(2027, 1))
        saveMonth(YearMonth.of(2027, 12))

        assertEquals(
            listOf(LocalDate.of(2026, 12, 31), LocalDate.of(2027, 1, 1)),
            loadDates(LocalDate.of(2026, 12, 31))
        )
    }

    @Test
    fun `only returns the asked zone's prayers`() = runTest {
        val otherZone = insertZone("WLY01")
        saveMonth(YearMonth.of(2026, 11), zone = otherZone)

        assertEquals(emptyList<LocalDate>(), loadDates(LocalDate.of(2026, 11, 15)))
        assertEquals(2, loadDates(LocalDate.of(2026, 11, 15), zone = otherZone).size)
    }

    @Test
    fun `saving a month again replaces its days`() = runTest {
        saveMonth(YearMonth.of(2026, 11))
        saveMonth(YearMonth.of(2026, 11))

        assertEquals(2, loadDates(LocalDate.of(2026, 11, 15)).size)
    }

    @Test
    fun `cleanup deletes every month before the given one`() = runTest {
        saveMonth(YearMonth.of(2026, 9))
        saveMonth(YearMonth.of(2026, 10))
        saveMonth(YearMonth.of(2026, 11))

        val deleted = dao.deletePrayerTimesBefore(year = 2026, month = 11)

        assertEquals(2, deleted)
        assertEquals(emptyList<LocalDate>(), loadDates(LocalDate.of(2026, 9, 15)))
        assertEquals(emptyList<LocalDate>(), loadDates(LocalDate.of(2026, 10, 15)))
        assertEquals(2, loadDates(LocalDate.of(2026, 11, 15)).size)
    }
}
