package com.kwanzatukule.features.catalogue.data.di

import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import com.kwanzatukule.features.catalogue.data.CatalogueRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: CatalogueRepositoryImpl,
    ): CatalogueRepository
}