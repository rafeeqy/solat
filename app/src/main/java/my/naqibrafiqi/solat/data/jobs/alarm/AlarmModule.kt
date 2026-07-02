package my.naqibrafiqi.solat.data.jobs.alarm

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.domain.repository.SolatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {
    @Singleton
    @Provides
    fun provideAlarmScheduler(
        @ApplicationContext context: Context,
        solatRepository: SolatRepository
    ): AlarmSchedulerRepository {
        return OfflineAlarmSchedulerRepository(context, solatRepository)
    }
}
