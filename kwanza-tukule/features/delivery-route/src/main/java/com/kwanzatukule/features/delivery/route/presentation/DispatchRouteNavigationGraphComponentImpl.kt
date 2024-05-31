package com.kwanzatukule.features.delivery.route.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.cart.presentation.list.ShoppingListComponent
import com.kwanzatukule.features.cart.presentation.list.ShoppingListComponentImpl
import com.kwanzatukule.features.delivery.route.presentation.DispatchRouteNavigationGraphComponent.Child
import com.kwanzatukule.features.delivery.route.presentation.DispatchRouteNavigationGraphComponent.Configuration
import com.kwanzatukule.features.delivery.route.presentation.route.DispatchRouteNavigationComponent
import com.kwanzatukule.features.delivery.route.presentation.route.DispatchRouteNavigationComponentImpl
import com.kwanzatukule.features.order.data.Filter
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.Result
import com.kwanzatukule.features.order.presentation.list.OrderListEvent
import com.kwanzatukule.features.order.presentation.utils.asUiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DispatchRouteNavigationGraphComponentImpl(
    context: ComponentContext,
    private val repository: OrderRepository,
    private val orderStatus: Order.Status?,
    component: DispatchRouteNavigationGraphComponent,
) : DispatchRouteNavigationGraphComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _event = Channel<OrderListEvent>()
    override val event: Flow<OrderListEvent> = _event.receiveAsFlow()

    override fun findNavigableLocations() {
        componentScope.launch {
            when (val result =
                repository.getDirectionNavigation(null, Filter(status = orderStatus))) {
                is Result.Failure -> _event.send(
                    OrderListEvent.Error(
                        result.error.asUiText(),
                        result.error,
                    )
                )

                is Result.Success ->
                    _event.send(OrderListEvent.FindNavigableLocations(result.data))
            }
        }
    }

    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.DispatchRoute(null),
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.DispatchRoute -> Child.DispatchRoute(
                component = DispatchRouteNavigationComponentImpl(
                    context = context,
                    repository = repository,
                    orderStatus = orderStatus,
                    component = object : DispatchRouteNavigationComponent {
                        override fun handleBackPress() {
                            this@DispatchRouteNavigationGraphComponentImpl.handleBackPress()
                        }

                        override fun onClickViewShoppingList(order: Order) {
                            navigation.push(Configuration.ShoppingList(order))
                        }
                    },
                ),
            )

            is Configuration.ShoppingList -> Child.ShoppingList(
                component = ShoppingListComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : ShoppingListComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }
                    },
                ),
            )
        }
    }
}