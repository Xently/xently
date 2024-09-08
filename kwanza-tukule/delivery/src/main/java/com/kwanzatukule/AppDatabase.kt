package com.kwanzatukule

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import co.ke.xently.libraries.data.local.InstantConverter
import co.ke.xently.libraries.data.local.ServerResponseCache
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import com.kwanzatukule.features.authentication.data.UserEntity
import com.kwanzatukule.features.cart.data.ShoppingCartDatabase
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.data.RoomTypeConverters.ProductConverter

@Database(
    version = 2,
    entities = [
        ServerResponseCache::class,
        UserEntity::class,
        ShoppingCart.Item::class,
    ],
)
@TypeConverters(
    InstantConverter::class,
    ProductConverter::class,
)
abstract class AppDatabase : RoomDatabase(),
    AuthenticationDatabase,
    ShoppingCartDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
