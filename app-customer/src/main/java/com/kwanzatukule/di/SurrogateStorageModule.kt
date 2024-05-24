package com.kwanzatukule.di

import com.kwanzatukule.AppDatabase
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import com.kwanzatukule.features.cart.data.ShoppingCartDatabase
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
}