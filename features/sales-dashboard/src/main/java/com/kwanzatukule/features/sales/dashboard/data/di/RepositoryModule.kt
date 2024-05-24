package com.kwanzatukule.features.sales.dashboard.data.di

import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardRepository
import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: SalesDashboardRepositoryImpl,
    ): SalesDashboardRepository
}