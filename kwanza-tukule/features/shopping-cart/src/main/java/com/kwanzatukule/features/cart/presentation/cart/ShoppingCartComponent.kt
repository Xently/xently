package com.kwanzatukule.features.cart.presentation.cart

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.domain.ShoppingCart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ShoppingCartComponent {
    val uiState: Value<ShoppingCartUiState> get() = throw NotImplementedError()
    val event: Flow<ShoppingCartEvent> get() = flow { }
    fun handleBackPress()
    fun incrementQuantity(item: ShoppingCart.Item) {}
    fun decrementQuantity(item: ShoppingCart.Item) {}
    fun remove(item: ShoppingCart.Item) {}
    fun updateQuantity(line: ShoppingCart.Item, quantity: String) {}
    fun handleCheckout()

    class Fake(state: ShoppingCartUiState) : ShoppingCartComponent {
        override val uiState: Value<ShoppingCartUiState> = MutableValue(state)
        override fun handleBackPress() {}
        override fun handleCheckout() {}
    }
}