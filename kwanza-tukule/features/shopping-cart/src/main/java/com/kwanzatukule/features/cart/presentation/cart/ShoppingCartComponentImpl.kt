package com.kwanzatukule.features.cart.presentation.cart

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.cart.domain.ShoppingCart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ShoppingCartComponentImpl(
    context: ComponentContext,
    component: ShoppingCartComponent,
    private val repository: ShoppingCartRepository,
) : ShoppingCartComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _uiState = MutableValue(ShoppingCartUiState())
    override val uiState: Value<ShoppingCartUiState> = _uiState

    private val _event = Channel<ShoppingCartEvent>()
    override val event: Flow<ShoppingCartEvent> = _event.receiveAsFlow()

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    override fun incrementQuantity(item: ShoppingCart.Item) {
        componentScope.launch {
            repository.incrementShoppingCartQuantity(item.product)
        }
    }

    override fun decrementQuantity(item: ShoppingCart.Item) {
        componentScope.launch {
            repository.decrementShoppingCartQuantity(item.product)
        }
    }

    override fun remove(item: ShoppingCart.Item) {
        componentScope.launch {
            repository.addToOrRemoveFromShoppingCart(item.product.copy(inShoppingCart = false))
        }
    }

    override fun updateQuantity(line: ShoppingCart.Item, quantity: String) {
        componentScope.launch {
            val qty = try {
                quantity.toInt()
            } catch (ex: NumberFormatException) {
                line.quantity
                // TODO: Show error...
//                _event.send(ShoppingCartEvent.Error)
            }
            repository.setShoppingCartQuantity(line.product, qty)
        }
    }
}