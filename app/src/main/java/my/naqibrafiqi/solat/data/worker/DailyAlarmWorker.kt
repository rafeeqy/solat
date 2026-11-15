package my.naqibrafiqi.solat.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import my.naqibrafiqi.solat.domain.usecase.RefreshPrayerAlarmsUseCase

/**
 * Schedules the prayer alarms for today and tomorrow. Runs once a day, and once straight away
 * whenever something may have made the alarms out of date (see [enqueueNow]).
 */
@HiltWorker
class DailyAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshPrayerAlarms: RefreshPrayerAlarmsUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            when (refreshPrayerAlarms()) {
                RefreshPrayerAlarmsUseCase.Result.Scheduled -> Result.success()
                RefreshPrayerAlarmsUseCase.Result.NoZone -> Result.success()
                // Usually no internet connection; WorkManager tries again with backoff.
                RefreshPrayerAlarmsUseCase.Result.Incomplete -> Result.retry()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Couldn't refresh the prayer alarms", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DailyAlarmWorker"

        const val PERIODIC_WORK_NAME = "DailyPrayerAlarmWork"
        private const val ONE_TIME_WORK_NAME = "RefreshPrayerAlarmsNow"

        /**
         * Refreshes the alarms now: after an alarm goes off (to schedule that prayer's next one),
         * after a reboot, an app update or a clock change, and when exact alarms are allowed.
         */
        fun enqueueNow(workManager: WorkManager) {
            workManager.enqueueUniqueWork(
                ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<DailyAlarmWorker>().build()
            )
        }
    }
}
