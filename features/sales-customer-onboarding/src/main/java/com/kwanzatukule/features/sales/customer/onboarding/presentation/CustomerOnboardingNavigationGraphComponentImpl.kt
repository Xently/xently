package com.kwanzatukule.features.sales.customer.onboarding.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraphComponent
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraphComponentImpl
import com.kwanzatukule.features.route.presentation.RouteNavigationGraphComponent
import com.kwanzatukule.features.route.presentation.RouteNavigationGraphComponentImpl
import com.kwanzatukule.features.sales.customer.onboarding.data.CustomerOnboardingRepository
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraphComponent.Child
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraphComponent.Configuration
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route

class CustomerOnboardingNavigationGraphComponentImpl(
    context: ComponentContext,
    component: CustomerOnboardingNavigationGraphComponent,
    private val navigateInIsolation: Boolean,
    private val repository: CustomerOnboardingRepository,
) : CustomerOnboardingNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.RouteList,
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.RouteList -> Child.RouteList(
                component = RouteNavigationGraphComponentImpl(
                    context = context,
                    repository = repository,
                    navigateInIsolation = navigateInIsolation,
                    component = object : RouteNavigationGraphComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }

                        override fun onClickRoute(route: Route) {
                            if (navigateInIsolation) {
                                navigation.push(Configuration.CustomerList(route = route))
                            } else {
                                this@CustomerOnboardingNavigationGraphComponentImpl.onClickRoute(
                                    route
                                )
                            }
                        }

                        override fun onClickRouteEntry() {
                            this@CustomerOnboardingNavigationGraphComponentImpl.onClickRouteEntry()
                        }
                    },
                ),
            )

            is Configuration.CustomerList -> Child.CustomerList(
                component = CustomerNavigationGraphComponentImpl(
                    route = config.route,
                    context = context,
                    component = object : CustomerNavigationGraphComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }

                        override fun onClickCustomer(customer: Customer) {
                            this@CustomerOnboardingNavigationGraphComponentImpl.onClickCustomer(
                                route = config.route,
                                customer = customer,
                            )
                        }
                    },
                    repository = repository,
                ),
            )
        }
    }
}