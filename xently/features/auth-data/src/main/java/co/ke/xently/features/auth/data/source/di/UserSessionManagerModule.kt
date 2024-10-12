package co.ke.xently.features.auth.data.source.di

import co.ke.xently.features.auth.data.TokenManagerImpl
import co.ke.xently.libraries.data.network.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class UserSessionManagerModule {
    @Binds
    abstract fun bindTokenManager(manager: TokenManagerImpl): TokenManager
}
