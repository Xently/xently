package com.kwanzatukule.features.customer.landing.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.customer.home.presentation.HomeComponent
import kotlinx.serialization.Serializable

interface LandingNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun onSignInRequested()
    fun onSignOutRequested()
    fun navigateToCatalogue(category: Category?)
    fun navigateToProductDetail(product: Product)
    fun navigateToShoppingCart()

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object Home : Configuration()
    }
}
