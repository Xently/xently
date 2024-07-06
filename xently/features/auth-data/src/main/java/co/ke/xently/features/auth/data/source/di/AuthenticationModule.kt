package co.ke.xently.features.auth.data.source.di

import co.ke.xently.features.auth.data.source.UserRepository
import co.ke.xently.features.auth.data.source.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthenticationModule {
    @Binds
    abstract fun bindsUserRepository(repository: UserRepositoryImpl): UserRepository
}