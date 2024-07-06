package co.ke.xently.features.products.data.source.di

import co.ke.xently.features.products.data.source.ProductRepository
import co.ke.xently.features.products.data.source.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProductModule {
    @Binds
    abstract fun bindsProductRepository(repository: ProductRepositoryImpl): ProductRepository
}