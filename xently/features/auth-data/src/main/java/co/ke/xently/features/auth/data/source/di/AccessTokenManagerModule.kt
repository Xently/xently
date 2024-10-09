package co.ke.xently.features.auth.data.source.di

import co.ke.xently.libraries.data.network.AccessTokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class AccessTokenManagerModule {
    @Binds
    abstract fun bindAccessTokenManager(manager: AccessTokenManagerImpl): AccessTokenManager
}
