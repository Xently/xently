package com.kwanzatukule.di

import com.kwanzatukule.AppDatabase
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import com.kwanzatukule.features.cart.data.ShoppingCartDatabase
import com.kwanzatukule.libraries.data.customer.data.CustomerDatabase
import com.kwanzatukule.libraries.data.route.data.RouteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SurrogateStorageModule {
    @Binds
    abstract fun bindAuthenticationDatabase(
        database: AppDatabase,
    ): AuthenticationDatabase

    @Binds
    abstract fun bindShoppingCartDatabase(
        database: AppDatabase,
    ): ShoppingCartDatabase

    @Binds
    abstract fun bindRouteDatabase(
        database: AppDatabase,
    ): RouteDatabase

    @Binds
    abstract fun bindCustomerDatabase(
        database: AppDatabase,
    ): CustomerDatabase
}