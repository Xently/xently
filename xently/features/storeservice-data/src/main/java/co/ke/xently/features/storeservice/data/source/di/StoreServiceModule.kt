package co.ke.xently.features.storeservice.data.source.di

import co.ke.xently.features.storeservice.data.source.StoreServiceRepository
import co.ke.xently.features.storeservice.data.source.StoreServiceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class StoreServiceModule {
    @Binds
    abstract fun bindsStoreServiceRepository(repository: StoreServiceRepositoryImpl): StoreServiceRepository
}