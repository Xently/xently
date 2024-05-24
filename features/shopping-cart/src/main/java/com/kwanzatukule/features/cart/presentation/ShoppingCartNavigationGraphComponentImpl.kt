package com.kwanzatukule.features.cart.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.cart.presentation.ShoppingCartNavigationGraphComponent.Child
import com.kwanzatukule.features.cart.presentation.ShoppingCartNavigationGraphComponent.Configuration
import com.kwanzatukule.features.cart.presentation.cart.ShoppingCartComponent
import com.kwanzatukule.features.cart.presentation.cart.ShoppingCartComponentImpl

class ShoppingCartNavigationGraphComponentImpl(
    context: ComponentContext,
    component: ShoppingCartNavigationGraphComponent,
    private val repository: ShoppingCartRepository,
) : ShoppingCartNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.ShoppingCart,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            Configuration.ShoppingCart -> Child.ShoppingCart(
                component = ShoppingCartComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : ShoppingCartComponent {
                        override fun handleBackPress() {
                            this@ShoppingCartNavigationGraphComponentImpl.onBackPress()
                        }

                        override fun handleCheckout() {
                            this@ShoppingCartNavigationGraphComponentImpl.handleCheckout()
                        }
                    }
                ),
            )
        }
    }
}