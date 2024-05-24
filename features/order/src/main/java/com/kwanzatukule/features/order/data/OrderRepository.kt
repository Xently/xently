package com.kwanzatukule.features.order.data

import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.domain.error.Result

interface OrderRepository {
    suspend fun placeOrder(order: Order): Result<Unit, DataError>
}