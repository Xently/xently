package com.kwanzatukule.features.customer.complaints.data.di

import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepository
import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: CustomerComplaintRepositoryImpl,
    ): CustomerComplaintRepository
}