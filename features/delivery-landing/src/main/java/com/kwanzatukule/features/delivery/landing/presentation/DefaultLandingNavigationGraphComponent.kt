package com.kwanzatukule.features.delivery.landing.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponent.Child
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponent.Configuration
import com.kwanzatukule.features.delivery.landing.presentation.home.DefaultHomeComponent
import com.kwanzatukule.features.delivery.landing.presentation.home.HomeComponent

class DefaultLandingNavigationGraphComponent(
    context: ComponentContext,
    component: LandingNavigationGraphComponent,
) : LandingNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.Home,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            Configuration.Home -> Child.Home(
                component = DefaultHomeComponent(
                    context = context,
                    component = object : HomeComponent {
                        override fun onSignInRequested() {
                            this@DefaultLandingNavigationGraphComponent.onSignInRequested()
                        }

                        override fun onSignOutRequested() {
                            this@DefaultLandingNavigationGraphComponent.onSignOutRequested()
                        }
                    }
                ),
            )
        }
    }
}