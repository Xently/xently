package com.kwanzatukule.features.delivery.home.data.di

import com.kwanzatukule.features.delivery.home.data.HomeRepository
import com.kwanzatukule.features.delivery.home.data.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: HomeRepositoryImpl,
    ): HomeRepository
}