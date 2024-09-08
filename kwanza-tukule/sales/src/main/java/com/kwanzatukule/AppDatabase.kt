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
import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintDatabase
import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintEntity
import com.kwanzatukule.features.customer.complaints.data.RoomTypeConverters.CustomerComplaintConverter
import com.kwanzatukule.features.sales.dashboard.data.RoomTypeConverters.SalesDashboardItemConverter
import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardDatabase
import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardItemEntity
import com.kwanzatukule.libraries.data.customer.data.CustomerDatabase
import com.kwanzatukule.libraries.data.customer.data.CustomerEntity
import com.kwanzatukule.libraries.data.customer.data.RoomTypeConverters.CustomerConverter
import com.kwanzatukule.libraries.data.route.data.RoomTypeConverters.RouteConverter
import com.kwanzatukule.libraries.data.route.data.RouteDatabase
import com.kwanzatukule.libraries.data.route.data.RouteEntity
import com.kwanzatukule.libraries.data.route.data.RouteSummaryEntity

@Database(
    version = 2,
    entities = [
        ServerResponseCache::class,
        UserEntity::class,
        ShoppingCart.Item::class,
        SalesDashboardItemEntity::class,
        RouteEntity::class,
        CustomerEntity::class,
        CustomerComplaintEntity::class,
        RouteSummaryEntity::class,
    ],
)
@TypeConverters(
    ProductConverter::class,
    SalesDashboardItemConverter::class,
    RouteConverter::class,
    CustomerConverter::class,
    CustomerComplaintConverter::class,
    InstantConverter::class,
)
abstract class AppDatabase : RoomDatabase(),
    AuthenticationDatabase,
    ShoppingCartDatabase,
    SalesDashboardDatabase,
    CustomerDatabase,
    RouteDatabase,
    CustomerComplaintDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
