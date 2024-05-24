package com.kwanzatukule.features.customer.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.presentation.entry.CustomerEntryComponent
import com.kwanzatukule.features.customer.presentation.list.CustomerListComponent
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.serialization.Serializable

interface CustomerNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun handleBackPress()
    fun onClickCustomer(customer: Customer)

    sealed class Child {
        data class CustomerList(val component: CustomerListComponent) : Child()
        data class CustomerEntry(val component: CustomerEntryComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class CustomerList(val route: Route) : Configuration()

        @Serializable
        data class CustomerEntry(val route: Route, val customer: Customer?) : Configuration()
    }
}