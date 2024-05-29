package com.kwanzatukule.features.order.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.presentation.list.ShoppingListComponent
import com.kwanzatukule.features.cart.presentation.list.ShoppingListComponentImpl
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.OrderNavigationGraphComponent.Child
import com.kwanzatukule.features.order.presentation.OrderNavigationGraphComponent.Configuration
import com.kwanzatukule.features.order.presentation.group.OrderGroupComponent
import com.kwanzatukule.features.order.presentation.group.OrderGroupComponentImpl

class OrderNavigationGraphComponentImpl(
    context: ComponentContext,
    private val repository: OrderRepository,
    component: OrderNavigationGraphComponent,
) : OrderNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.OrderGroup(null),
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.OrderGroup -> Child.OrderGroup(
                component = OrderGroupComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : OrderGroupComponent {
                        override fun handleBackPress() {
                            this@OrderNavigationGraphComponentImpl.handleBackPress()
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