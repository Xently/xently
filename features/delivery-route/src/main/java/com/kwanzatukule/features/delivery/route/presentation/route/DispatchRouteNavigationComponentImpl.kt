package com.kwanzatukule.features.delivery.route.presentation.route

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.route.domain.Page
import com.kwanzatukule.features.delivery.route.presentation.route.DispatchRouteNavigationComponent.Child
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.list.OrderListComponent
import com.kwanzatukule.features.order.presentation.list.OrderListComponentImpl
import kotlinx.serialization.serializer

@OptIn(ExperimentalDecomposeApi::class)
class DispatchRouteNavigationComponentImpl(
    context: ComponentContext,
    component: DispatchRouteNavigationComponent,
    private val repository: OrderRepository,
    private val orderStatus: Order.Status?,
) : DispatchRouteNavigationComponent by component, ComponentContext by context {
    private val navigation = PagesNavigation<Page>()
    override val childPages: Value<ChildPages<*, Child>> = childPages(
        source = navigation,
        serializer = serializer<Page>(),
        initialPages = {
            Pages(
                items = Page.entries,
                selectedIndex = Page.entries.first().ordinal,
            )
        },
        childFactory = ::createChild,
    )

    override fun selectPage(index: Int) {
        navigation.select(index = index)
    }

    private fun createChild(config: Page, context: ComponentContext): Child {
        return Child.OrderList(
            page = config,
            component = OrderListComponentImpl(
                context = context,
                status = orderStatus,
                repository = repository,
                component = object : OrderListComponent {
                    override fun handleBackPress() {
                        this@DispatchRouteNavigationComponentImpl.handleBackPress()
                    }

                    override fun onClickViewShoppingList(order: Order) {
                        this@DispatchRouteNavigationComponentImpl.onClickViewShoppingList(order)
                    }
                }
            ),
        )
    }
}