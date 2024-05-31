package com.kwanzatukule.features.order.presentation.group

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.list.OrderListComponent

interface OrderGroupComponent {
    @OptIn(ExperimentalDecomposeApi::class)
    val childPages: Value<ChildPages<*, Child>> get() = throw NotImplementedError()
    fun selectPage(index: Int) {}
    fun handleBackPress()
    fun onClickViewShoppingList(order: Order)

    object Fake : OrderGroupComponent {
        override fun handleBackPress() {}
        override fun onClickViewShoppingList(order: Order) {}
    }

    sealed class Child {
        data class Status(val component: OrderListComponent) : Child()
    }
}
