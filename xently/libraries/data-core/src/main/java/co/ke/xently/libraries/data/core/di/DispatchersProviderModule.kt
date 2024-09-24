package co.ke.xently.libraries.data.core.di

import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DispatchersProviderModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider {
        return DispatchersProvider.Default
    }
}