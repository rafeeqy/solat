package my.naqibrafiqi.solat.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import my.naqibrafiqi.solat.data.local.db.dao.SolatDao
import my.naqibrafiqi.solat.util.SolatDateTime
import java.time.LocalDate

@HiltWorker
class PrayerTimeCleanupWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val solatDao: SolatDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val now = LocalDate.now(SolatDateTime.ZONE)

            // Keep this month and later; delete every earlier month, including any that a
            // late or skipped run missed.
            val rowsDeleted = solatDao.deletePrayerTimesBefore(
                year = now.year,
                month = now.monthValue
            )

            Log.i("MONTHLY_CLEANUP", "Prayers deleted: $rowsDeleted")

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()

            Result.retry()
        }
    }
}