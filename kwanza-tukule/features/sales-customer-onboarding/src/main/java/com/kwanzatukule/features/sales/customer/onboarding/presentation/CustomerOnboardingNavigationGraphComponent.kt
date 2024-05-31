package com.kwanzatukule.features.sales.customer.onboarding.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraphComponent
import com.kwanzatukule.features.route.presentation.RouteNavigationGraphComponent
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.serialization.Serializable

interface CustomerOnboardingNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun handleBackPress()
    fun onClickRoute(route: Route)
    fun onClickRouteEntry()
    fun onClickCustomer(route: Route, customer: Customer)

    sealed class Child {
        data class RouteList(val component: RouteNavigationGraphComponent) : Child()
        data class CustomerList(val component: CustomerNavigationGraphComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object RouteList : Configuration()

        @Serializable
        data class CustomerList(val route: Route) : Configuration()
    }
}