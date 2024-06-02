package co.ke.xently.features.storecategory.data.source.di

import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class StoreCategoryModule {
    @Binds
    abstract fun bindsStoreCategoryRepository(repository: StoreCategoryRepositoryImpl): StoreCategoryRepository
}