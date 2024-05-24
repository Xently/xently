package com.kwanzatukule.libraries.data.customer.data.di

import com.kwanzatukule.libraries.data.customer.data.CustomerRepository
import com.kwanzatukule.libraries.data.customer.data.CustomerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: CustomerRepositoryImpl,
    ): CustomerRepository
}