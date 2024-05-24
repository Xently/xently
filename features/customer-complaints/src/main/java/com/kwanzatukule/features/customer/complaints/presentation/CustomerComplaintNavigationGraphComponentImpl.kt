package com.kwanzatukule.features.customer.complaints.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepository
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraphComponent.Child
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraphComponent.Configuration
import com.kwanzatukule.features.customer.complaints.presentation.entry.CustomerComplaintEntryComponent
import com.kwanzatukule.features.customer.complaints.presentation.entry.CustomerComplaintEntryComponentImpl
import com.kwanzatukule.features.customer.complaints.presentation.list.CustomerComplaintListComponent
import com.kwanzatukule.features.customer.complaints.presentation.list.CustomerComplaintListComponentImpl
import com.kwanzatukule.libraries.data.customer.domain.Customer

class CustomerComplaintNavigationGraphComponentImpl(
    customer: Customer?,
    context: ComponentContext,
    component: CustomerComplaintNavigationGraphComponent,
    private val navigateInIsolation: Boolean,
    private val repository: CustomerComplaintRepository,
) : CustomerComplaintNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.CustomerComplaintList(customer = customer),
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.CustomerComplaintList -> Child.CustomerComplaintList(
                component = CustomerComplaintListComponentImpl(
                    context = context,
                    customer = config.customer,
                    repository = repository,
                    component = object : CustomerComplaintListComponent {
                        override fun onClickCustomerComplaintEntry() {
                            navigation.push(
                                Configuration.CustomerComplaintEntry(
                                    customer = config.customer,
                                    complaint = null,
                                )
                            )
                        }

                        override fun handleBackPress() {
                            this@CustomerComplaintNavigationGraphComponentImpl.handleBackPress()
                        }
                    },
                ),
            )

            is Configuration.CustomerComplaintEntry -> Child.CustomerComplaintEntry(
                component = CustomerComplaintEntryComponentImpl(
                    customer = config.customer,
                    context = context,
                    repository = repository,
                    component = object : CustomerComplaintEntryComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }
                    },
                ),
            )
        }
    }
}