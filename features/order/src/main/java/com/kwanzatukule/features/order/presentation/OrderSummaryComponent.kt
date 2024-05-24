package com.kwanzatukule.features.order.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface OrderSummaryComponent {
    val uiState: Value<OrderSummaryUiState> get() = throw NotImplementedError()
    val event: Flow<OrderSummaryEvent> get() = flow { }
    fun handleBackPress()
    fun onOrderPlaced()
    fun onClickPlaceOrder() {}
    fun onClickUpdateRoute()
    fun onClickUpdateCustomer()
    fun onClickUpdateShoppingCart()

    data class Fake(val state: OrderSummaryUiState) : OrderSummaryComponent {
        override val uiState = MutableValue(state)
        override fun handleBackPress() {}
        override fun onOrderPlaced() {}
        override fun onClickUpdateRoute() {}
        override fun onClickUpdateCustomer() {}
        override fun onClickUpdateShoppingCart() {}
    }
}