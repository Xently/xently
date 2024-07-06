package co.ke.xently.features.stores.data.source.di

import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.stores.data.source.StoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class StoreModule {
    @Binds
    abstract fun bindsStoreRepository(repository: StoreRepositoryImpl): StoreRepository
}