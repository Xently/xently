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
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.features.products.data.source.local.ProductEntity
import co.ke.xently.features.products.data.source.local.RoomTypeConverters.ProductConverter
import co.ke.xently.features.reviews.data.source.local.ReviewCategoryDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewCategoryEntity
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewEntity
import co.ke.xently.features.reviews.data.source.local.RoomTypeConverters.ReviewCategoryConverter
import co.ke.xently.features.reviews.data.source.local.RoomTypeConverters.ReviewConverter
import co.ke.xently.features.shops.data.source.local.RoomTypeConverters.ShopConverter
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.features.shops.data.source.local.ShopEntity
import co.ke.xently.features.stores.data.source.local.RoomTypeConverters.StoreConverter
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.stores.data.source.local.StoreEntity

@Database(
    version = 1,
    entities = [
        User::class,
        ShopEntity::class,
        StoreEntity::class,
        ReviewEntity::class,
        ReviewCategoryEntity::class,
        ProductEntity::class,
        CustomerEntity::class,
    ],
)
@TypeConverters(
    ShopConverter::class,
    StoreConverter::class,
    ReviewConverter::class,
    ReviewCategoryConverter::class,
    ProductConverter::class,
    CustomerConverter::class,
)
abstract class AppDatabase : RoomDatabase(),
    AuthenticationDatabase,
    ShopDatabase,
    StoreDatabase,
    ReviewDatabase,
    ReviewCategoryDatabase,
    ProductDatabase,
    CustomerDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
