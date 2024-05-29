package com.kwanzatukule.features.delivery.route.presentation.route

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.route.domain.Page
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.list.OrderListComponent

interface DispatchRouteNavigationComponent {
    @OptIn(ExperimentalDecomposeApi::class)
    val childPages: Value<ChildPages<*, Child>> get() = throw NotImplementedError()
    fun selectPage(index: Int) {}
    fun handleBackPress()
    fun onClickViewShoppingList(order: Order)

    sealed class Child {
        data class OrderList(val page: Page, val component: OrderListComponent) : Child()
    }
}