package com.kwanzatukule

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import com.kwanzatukule.features.authentication.data.UserEntity
import com.kwanzatukule.features.cart.data.ShoppingCartDatabase
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.data.RoomTypeConverters.ProductConverter
import com.kwanzatukule.libraries.data.customer.data.CustomerDatabase
import com.kwanzatukule.libraries.data.customer.data.CustomerEntity
import com.kwanzatukule.libraries.data.customer.data.RoomTypeConverters.CustomerConverter
import com.kwanzatukule.libraries.data.route.data.RoomTypeConverters.RouteConverter
import com.kwanzatukule.libraries.data.route.data.RouteDatabase
import com.kwanzatukule.libraries.data.route.data.RouteEntity
import com.kwanzatukule.libraries.data.route.data.RouteSummaryEntity

@Database(
    version = 1,
    entities = [
        UserEntity::class,
        ShoppingCart.Item::class,
        RouteEntity::class,
        CustomerEntity::class,
        RouteSummaryEntity::class,
    ],
)
@TypeConverters(
    ProductConverter::class,
    RouteConverter::class,
    CustomerConverter::class,
)
abstract class AppDatabase : RoomDatabase(),
    AuthenticationDatabase,
    CustomerDatabase,
    RouteDatabase,
    ShoppingCartDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
