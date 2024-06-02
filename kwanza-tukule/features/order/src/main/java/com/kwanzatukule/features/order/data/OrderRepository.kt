package com.kwanzatukule.features.order.data

import co.ke.xently.libraries.location.tracker.domain.DirectionNavigation
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.pagination.data.PagedResponse
import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.domain.error.Result

interface OrderRepository : ShoppingCartRepository {
    suspend fun placeOrder(order: Order): Result<Unit, DataError>
    suspend fun getOrders(url: String?, filter: Filter): PagedResponse<Order>
    suspend fun getDirectionNavigation(
        url: String?,
        filter: Filter,
    ): Result<DirectionNavigation, DataError>

    suspend fun getMidLocation(url: String?, filter: Filter): Result<Location?, DataError>
}