package com.kwanzatukule.features.customer.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraphComponent.Child
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraphComponent.Configuration
import com.kwanzatukule.features.customer.presentation.entry.CustomerEntryComponent
import com.kwanzatukule.features.customer.presentation.entry.CustomerEntryComponentImpl
import com.kwanzatukule.features.customer.presentation.list.CustomerListComponent
import com.kwanzatukule.features.customer.presentation.list.CustomerListComponentImpl
import com.kwanzatukule.libraries.data.customer.data.CustomerRepository
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route

class CustomerNavigationGraphComponentImpl(
    route: Route,
    context: ComponentContext,
    component: CustomerNavigationGraphComponent,
    private val repository: CustomerRepository,
) : CustomerNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.CustomerList(route = route),
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.CustomerList -> Child.CustomerList(
                component = CustomerListComponentImpl(
                    context = context,
                    route = config.route,
                    repository = repository,
                    component = object : CustomerListComponent {
                        override fun onClickCustomerEntry() {
                            navigation.push(
                                Configuration.CustomerEntry(
                                    route = config.route,
                                    customer = null,
                                )
                            )
                        }

                        override fun handleBackPress() {
                            this@CustomerNavigationGraphComponentImpl.handleBackPress()
                        }

                        override fun onClickCustomer(customer: Customer) {
                            this@CustomerNavigationGraphComponentImpl.onClickCustomer(customer)
                        }
                    },
                ),
            )

            is Configuration.CustomerEntry -> Child.CustomerEntry(
                component = CustomerEntryComponentImpl(
                    route = config.route,
                    context = context,
                    component = object : CustomerEntryComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }
                    },
                    repository = repository,
                ),
            )
        }
    }
}