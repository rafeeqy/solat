package my.naqibrafiqi.solat.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.util.SolatDateTime
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * The current time, in Malaysia time ([SolatDateTime.ZONE]). Code that needs "now" or "today"
     * takes this instead of calling `LocalDate.now()`, so it ignores the phone's time zone and
     * tests can fix the time.
     */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.system(SolatDateTime.ZONE)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
