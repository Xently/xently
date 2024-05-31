package com.kwanzatukule.libraries.data.route.data.di

import com.kwanzatukule.libraries.data.route.data.RouteRepository
import com.kwanzatukule.libraries.data.route.data.RouteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: RouteRepositoryImpl,
    ): RouteRepository
}