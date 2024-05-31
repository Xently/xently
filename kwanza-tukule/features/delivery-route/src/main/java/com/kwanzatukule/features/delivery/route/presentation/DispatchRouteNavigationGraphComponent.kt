package com.kwanzatukule.features.delivery.route.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.presentation.list.ShoppingListComponent
import com.kwanzatukule.features.delivery.route.presentation.route.DispatchRouteNavigationComponent
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.list.OrderListEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

interface DispatchRouteNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    val event: Flow<OrderListEvent> get() = flow { }
    fun handleBackPress()
    fun findNavigableLocations() {}

    sealed class Child {
        data class DispatchRoute(val component: DispatchRouteNavigationComponent) : Child()
        data class ShoppingList(val component: ShoppingListComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class DispatchRoute(val status: Order.Status?) : Configuration()

        @Serializable
        data class ShoppingList(val order: Order) : Configuration()
    }
}
