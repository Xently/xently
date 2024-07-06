package co.ke.xently.features.openinghours.data.source.di

import co.ke.xently.features.openinghours.data.source.OpeningHourRepository
import co.ke.xently.features.openinghours.data.source.OpeningHourRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OpeningHourModule {
    @Binds
    abstract fun bindsOpeningHourRepository(repository: OpeningHourRepositoryImpl): OpeningHourRepository
}