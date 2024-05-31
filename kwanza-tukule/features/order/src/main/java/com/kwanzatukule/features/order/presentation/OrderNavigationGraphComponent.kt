package com.kwanzatukule.features.order.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.presentation.list.ShoppingListComponent
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.group.OrderGroupComponent
import kotlinx.serialization.Serializable

interface OrderNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun handleBackPress()

    sealed class Child {
        data class OrderGroup(val component: OrderGroupComponent) : Child()
        data class ShoppingList(val component: ShoppingListComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class OrderGroup(val status: Order.Status?) : Configuration()

        @Serializable
        data class ShoppingList(val order: Order) : Configuration()
    }
}
