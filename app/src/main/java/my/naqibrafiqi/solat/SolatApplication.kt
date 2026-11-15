package my.naqibrafiqi.solat

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import my.naqibrafiqi.solat.data.worker.DailyAlarmWorker
import my.naqibrafiqi.solat.data.worker.PrayerTimeCleanupWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class SolatApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupBackgroundTasks()
    }

    private fun setupBackgroundTasks() {
        val constraints =
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

        val cleanupWorkRequest =
            PeriodicWorkRequestBuilder<PrayerTimeCleanupWorker>(32, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        // No constraints: the alarms matter more than the battery, and the work is small.
        val dailyAlarmWorkRequest =
            PeriodicWorkRequestBuilder<DailyAlarmWorker>(1, TimeUnit.DAYS)
                .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            "CleanPrayerTimeWork",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWorkRequest
        )

        // UPDATE (not KEEP) so installs from 1.0.x drop the old battery-not-low constraint.
        workManager.enqueueUniquePeriodicWork(
            DailyAlarmWorker.PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyAlarmWorkRequest
        )
    }
}
