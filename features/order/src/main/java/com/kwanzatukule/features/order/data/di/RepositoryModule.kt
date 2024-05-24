package com.kwanzatukule.features.order.data.di

import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.data.OrderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: OrderRepositoryImpl,
    ): OrderRepository
}