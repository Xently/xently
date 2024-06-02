package co.ke.xently.features.shops.data.source.di

import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.shops.data.source.ShopRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ShopModule {
    @Binds
    abstract fun bindsShopRepository(repository: ShopRepositoryImpl): ShopRepository
}