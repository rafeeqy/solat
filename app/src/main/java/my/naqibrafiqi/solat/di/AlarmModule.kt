package my.naqibrafiqi.solat.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.naqibrafiqi.solat.data.alarm.OfflineSolatAlarmScheduler
import my.naqibrafiqi.solat.domain.alarm.SolatAlarmScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(solatAlarmScheduler: OfflineSolatAlarmScheduler): SolatAlarmScheduler
}
