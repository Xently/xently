package co.ke.xently.customer.di

import co.ke.xently.customer.AppDatabase
import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewCategoryDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.features.stores.data.source.local.StoreDatabase
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
    abstract fun bindShopDatabase(
        database: AppDatabase,
    ): ShopDatabase

    @Binds
    abstract fun bindStoreDatabase(
        database: AppDatabase,
    ): StoreDatabase

    @Binds
    abstract fun bindReviewDatabase(
        database: AppDatabase,
    ): ReviewDatabase

    @Binds
    abstract fun bindReviewCategoryDatabase(
        database: AppDatabase,
    ): ReviewCategoryDatabase

    @Binds
    abstract fun bindProductDatabase(
        database: AppDatabase,
    ): ProductDatabase

    @Binds
    abstract fun bindCustomerDatabase(
        database: AppDatabase,
    ): CustomerDatabase
}