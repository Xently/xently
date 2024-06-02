package co.ke.xently.features.merchant.data.source.di

import co.ke.xently.features.merchant.data.source.MerchantRepository
import co.ke.xently.features.merchant.data.source.MerchantRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MerchantModule {
    @Binds
    abstract fun bindsMerchantRepository(repository: MerchantRepositoryImpl): MerchantRepository
}