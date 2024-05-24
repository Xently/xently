package com.kwanzatukule.features.cart.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.presentation.cart.ShoppingCartComponent
import kotlinx.serialization.Serializable

interface ShoppingCartNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()

    fun onBackPress()
    fun handleCheckout()

    sealed class Child {
        data class ShoppingCart(val component: ShoppingCartComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object ShoppingCart : Configuration()
    }
}