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

        val dailyAlarmWorkRequest =
            PeriodicWorkRequestBuilder<DailyAlarmWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            "CleanPrayerTimeWork",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWorkRequest
        )

        workManager.enqueueUniquePeriodicWork(
            "DailyPrayerAlarmWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyAlarmWorkRequest
        )
    }
}
