package co.ke.xently.features.profile.data.source.di

import co.ke.xently.features.profile.data.source.ProfileStatisticRepository
import co.ke.xently.features.profile.data.source.ProfileStatisticRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProfileModule {
    @Binds
    abstract fun bindsProfileRepository(repository: ProfileStatisticRepositoryImpl): ProfileStatisticRepository
}