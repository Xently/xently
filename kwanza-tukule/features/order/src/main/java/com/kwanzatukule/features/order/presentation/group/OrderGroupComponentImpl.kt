package com.kwanzatukule.features.order.presentation.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.group.OrderGroupComponent.Child
import com.kwanzatukule.features.order.presentation.list.OrderListComponent
import com.kwanzatukule.features.order.presentation.list.OrderListComponentImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.serializer

@OptIn(ExperimentalDecomposeApi::class)
class OrderGroupComponentImpl(
    context: ComponentContext,
    component: OrderGroupComponent,
    private val repository: OrderRepository,
) : OrderGroupComponent by component, ComponentContext by context {
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val navigation = PagesNavigation<Order.Status>()
    override val childPages: Value<ChildPages<*, Child>> = childPages(
        source = navigation,
        serializer = serializer<Order.Status>(),
        initialPages = {
            Pages(
                items = Order.Status.entries,
                selectedIndex = Order.Status.Pending.ordinal,
            )
        },
        childFactory = ::createChild,
    )

    override fun selectPage(index: Int) {
        navigation.select(index = index)
    }

    private fun createChild(config: Order.Status, context: ComponentContext): Child {
        return Child.Status(
            component = OrderListComponentImpl(
                context = context,
                repository = repository,
                status = config,
                component = object : OrderListComponent {
                    override fun handleBackPress() {
                        TODO("Not yet implemented")
                    }

                    override fun onClickViewShoppingList(order: Order) {
                        this@OrderGroupComponentImpl.onClickViewShoppingList(order)
                    }
                },
            ),
        )
    }
}