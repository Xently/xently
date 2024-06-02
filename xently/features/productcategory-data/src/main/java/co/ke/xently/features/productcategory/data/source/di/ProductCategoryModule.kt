package co.ke.xently.features.productcategory.data.source.di

import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProductCategoryModule {
    @Binds
    abstract fun bindsProductCategoryRepository(repository: ProductCategoryRepositoryImpl): ProductCategoryRepository
}