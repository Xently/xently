package com.kwanzatukule.features.cart.data.di

import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.cart.data.ShoppingCartRepositoryImpl
import com.kwanzatukule.features.catalogue.data.ShoppingCartChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: ShoppingCartRepositoryImpl,
    ): ShoppingCartRepository

    @Binds
    abstract fun bindShoppingCartChecker(
        repository: ShoppingCartRepositoryImpl,
    ): ShoppingCartChecker
}