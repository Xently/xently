package com.kwanzatukule.features.customer.landing.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.customer.home.presentation.HomeComponent
import com.kwanzatukule.features.customer.home.presentation.HomeComponentImpl
import com.kwanzatukule.features.customer.landing.data.LandingRepository
import com.kwanzatukule.features.customer.landing.presentation.LandingNavigationGraphComponent.Child
import com.kwanzatukule.features.customer.landing.presentation.LandingNavigationGraphComponent.Configuration

class LandingNavigationGraphComponentImpl(
    context: ComponentContext,
    component: LandingNavigationGraphComponent,
    private val landingRepository: LandingRepository,
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
                component = HomeComponentImpl(
                    context = context,
                    repository = landingRepository,
                    component = object : HomeComponent {
                        override fun onSignInRequested() {
                            this@LandingNavigationGraphComponentImpl.onSignInRequested()
                        }

                        override fun onSignOutRequested() {
                            this@LandingNavigationGraphComponentImpl.onSignOutRequested()
                        }

                        override fun navigateToCatalogue(category: Category?) {
                            this@LandingNavigationGraphComponentImpl.navigateToCatalogue(category)
                        }

                        override fun navigateToProductDetail(product: Product) {
                            this@LandingNavigationGraphComponentImpl.navigateToProductDetail(
                                product
                            )
                        }

                        override fun navigateToShoppingCart() {
                            this@LandingNavigationGraphComponentImpl.navigateToShoppingCart()
                        }
                    }
                ),
            )
        }
    }
}