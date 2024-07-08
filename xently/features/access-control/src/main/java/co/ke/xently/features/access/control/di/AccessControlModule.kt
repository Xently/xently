package co.ke.xently.features.access.control.di

import co.ke.xently.features.access.control.BuildConfig.BASE_URL
import co.ke.xently.libraries.data.network.BaseURL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AccessControlModule {
    @Provides
    @Singleton
    fun provideBaseUrl(): BaseURL {
        return BaseURL {
            BASE_URL
        }
    }
}