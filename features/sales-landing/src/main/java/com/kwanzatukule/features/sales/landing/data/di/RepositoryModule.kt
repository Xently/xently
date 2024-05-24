package com.kwanzatukule.features.sales.landing.data.di

import com.kwanzatukule.features.sales.landing.data.LandingRepository
import com.kwanzatukule.features.sales.landing.data.LandingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: LandingRepositoryImpl,
    ): LandingRepository
}