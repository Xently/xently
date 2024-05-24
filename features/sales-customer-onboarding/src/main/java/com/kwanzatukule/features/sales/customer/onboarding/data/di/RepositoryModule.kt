package com.kwanzatukule.features.sales.customer.onboarding.data.di

import com.kwanzatukule.features.sales.customer.onboarding.data.CustomerOnboardingRepository
import com.kwanzatukule.features.sales.customer.onboarding.data.CustomerOnboardingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: CustomerOnboardingRepositoryImpl,
    ): CustomerOnboardingRepository
}