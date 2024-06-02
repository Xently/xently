package co.ke.xently.features.customers.data.source.di

import co.ke.xently.features.customers.data.source.CustomerRepository
import co.ke.xently.features.customers.data.source.CustomerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CustomerModule {
    @Binds
    abstract fun bindsCustomerRepository(repository: CustomerRepositoryImpl): CustomerRepository
}