package co.ke.xently.features.access.control.data.di

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.access.control.data.AccessControlRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AbstractAccessControlModule {
    @Binds
    abstract fun bindsAccessControlRepository(repository: AccessControlRepositoryImpl): AccessControlRepository
}