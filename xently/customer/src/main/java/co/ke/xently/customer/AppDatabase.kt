package co.ke.xently.customer

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.User
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import co.ke.xently.features.customers.data.source.local.CustomerEntity
import co.ke.xently.features.customers.data.source.local.RoomTypeConverters.CustomerConverter
import co.ke.xently.features.notification.topic.data.source.local.NotificationTopicDatabase
import co.ke.xently.features.notification.topic.data.source.local.NotificationTopicEntity
import co.ke.xently.features.notification.topic.data.source.local.RoomTypeConverters.NotificationTopicConverter
import co.ke.xently.features.notifications.data.source.local.NotificationDatabase
import co.ke.xently.features.notifications.data.source.local.NotificationEntity
import co.ke.xently.features.notifications.data.source.local.RoomTypeConverters.NotificationConverter
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryEntity
import co.ke.xently.features.productcategory.data.source.local.RoomTypeConverters.ProductCategoryConverter
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.features.products.data.source.local.ProductEntity
import co.ke.xently.features.products.data.source.local.RoomTypeConverters.ProductConverter
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryDatabase
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryEntity
import co.ke.xently.features.reviewcategory.data.source.local.RoomTypeConverters.ReviewCategoryConverter
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewEntity
import co.ke.xently.features.reviews.data.source.local.RoomTypeConverters.ReviewConverter
import co.ke.xently.features.shops.data.source.local.RoomTypeConverters.ShopConverter
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.features.shops.data.source.local.ShopEntity
import co.ke.xently.features.storecategory.data.source.local.RoomTypeConverters.StoreCategoryConverter
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryDatabase
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryEntity
import co.ke.xently.features.stores.data.source.local.RoomTypeConverters.StoreConverter
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.stores.data.source.local.StoreEntity
import co.ke.xently.features.storeservice.data.source.local.RoomTypeConverters.StoreServiceConverter
import co.ke.xently.features.storeservice.data.source.local.StoreServiceDatabase
import co.ke.xently.features.storeservice.data.source.local.StoreServiceEntity

@Database(
    version = 1,
    entities = [
        User::class,
        ShopEntity::class,
        StoreEntity::class,
        StoreCategoryEntity::class,
        StoreServiceEntity::class,
        ReviewEntity::class,
        ReviewCategoryEntity::class,
        ProductEntity::class,
        ProductCategoryEntity::class,
        CustomerEntity::class,
        NotificationEntity::class,
        NotificationTopicEntity::class,
    ],
)
@TypeConverters(
    ShopConverter::class,
    StoreConverter::class,
    StoreCategoryConverter::class,
    StoreServiceConverter::class,
    ReviewConverter::class,
    ReviewCategoryConverter::class,
    ProductConverter::class,
    ProductCategoryConverter::class,
    CustomerConverter::class,
    NotificationConverter::class,
    NotificationTopicConverter::class,
)
abstract class AppDatabase : RoomDatabase(),
    AuthenticationDatabase,
    ShopDatabase,
    StoreDatabase,
    StoreCategoryDatabase,
    StoreServiceDatabase,
    ReviewDatabase,
    ReviewCategoryDatabase,
    ProductDatabase,
    ProductCategoryDatabase,
    CustomerDatabase,
    NotificationDatabase,
    NotificationTopicDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
