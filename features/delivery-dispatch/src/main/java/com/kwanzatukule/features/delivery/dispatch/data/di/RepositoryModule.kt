package com.kwanzatukule.features.delivery.dispatch.data.di

import com.kwanzatukule.features.delivery.dispatch.data.DispatchRepository
import com.kwanzatukule.features.delivery.dispatch.data.DispatchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: DispatchRepositoryImpl,
    ): DispatchRepository
}