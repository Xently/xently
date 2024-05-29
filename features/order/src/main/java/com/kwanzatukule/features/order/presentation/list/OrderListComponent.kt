package com.kwanzatukule.features.order.presentation.list

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.libraries.location.tracker.domain.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface OrderListComponent {
    val orders: Flow<PagingData<Order>> get() = throw NotImplementedError()

    val uiState: Value<OrderListUiState> get() = throw NotImplementedError()
    val event: Flow<OrderListEvent> get() = flow { }
    fun handleBackPress()
    fun onClickViewShoppingList(order: Order)
    fun setLocation(location: Location) {}

    data class Fake(
        val state: OrderListUiState,
        @Suppress("PropertyName") val _orders: PagingData<Order>,
    ) : OrderListComponent {
        override val uiState = MutableValue(state)
        override val orders = flowOf(_orders)
        override fun handleBackPress() {}
        override fun onClickViewShoppingList(order: Order) {}
    }
}
