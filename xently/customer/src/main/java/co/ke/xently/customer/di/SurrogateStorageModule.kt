package co.ke.xently.customer.di

import co.ke.xently.customer.AppDatabase
import co.ke.xently.features.access.control.data.local.AccessControlDatabase
import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import co.ke.xently.features.notification.topic.data.source.local.NotificationTopicDatabase
import co.ke.xently.features.notifications.data.source.local.NotificationDatabase
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.features.profile.data.source.local.ProfileStatisticDatabase
import co.ke.xently.features.recommendations.data.source.local.RecommendationDatabase
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryDatabase
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.storeservice.data.source.local.StoreServiceDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SurrogateStorageModule {
    @Binds
    abstract fun bindAccessControlDatabase(
        database: AppDatabase,
    ): AccessControlDatabase

    @Binds
    abstract fun bindAuthenticationDatabase(
        database: AppDatabase,
    ): AuthenticationDatabase

    @Binds
    abstract fun bindProfileStatisticDatabase(
        database: AppDatabase,
    ): ProfileStatisticDatabase

    @Binds
    abstract fun bindShopDatabase(
        database: AppDatabase,
    ): ShopDatabase

    @Binds
    abstract fun bindStoreDatabase(
        database: AppDatabase,
    ): StoreDatabase

    @Binds
    abstract fun bindStoreCategoryDatabase(
        database: AppDatabase,
    ): StoreCategoryDatabase

    @Binds
    abstract fun bindStoreServiceDatabase(
        database: AppDatabase,
    ): StoreServiceDatabase

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
    abstract fun bindProductCategoryDatabase(
        database: AppDatabase,
    ): ProductCategoryDatabase

    @Binds
    abstract fun bindCustomerDatabase(
        database: AppDatabase,
    ): CustomerDatabase

    @Binds
    abstract fun bindRecommendationDatabase(
        database: AppDatabase,
    ): RecommendationDatabase

    @Binds
    abstract fun bindNotificationDatabase(
        database: AppDatabase,
    ): NotificationDatabase

    @Binds
    abstract fun bindNotificationTopicDatabase(
        database: AppDatabase,
    ): NotificationTopicDatabase
}