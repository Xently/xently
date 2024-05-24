package com.kwanzatukule

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import com.kwanzatukule.features.authentication.data.User
import com.kwanzatukule.features.cart.data.ShoppingCartDatabase
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.data.RoomTypeConverters.ProductConverter

@Database(
    version = 1,
    entities = [
        User::class,
        ShoppingCart.Item::class,
    ],
)
@TypeConverters(
    ProductConverter::class,
)
abstract class AppDatabase : RoomDatabase(), AuthenticationDatabase, ShoppingCartDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
