package com.kwanzatukule.features.customer.complaints.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.features.customer.complaints.presentation.entry.CustomerComplaintEntryComponent
import com.kwanzatukule.features.customer.complaints.presentation.list.CustomerComplaintListComponent
import com.kwanzatukule.libraries.data.customer.domain.Customer
import kotlinx.serialization.Serializable

interface CustomerComplaintNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun handleBackPress()

    sealed class Child {
        data class CustomerComplaintList(val component: CustomerComplaintListComponent) : Child()
        data class CustomerComplaintEntry(val component: CustomerComplaintEntryComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class CustomerComplaintList(val customer: Customer?) : Configuration()

        @Serializable
        data class CustomerComplaintEntry(
            val customer: Customer?,
            val complaint: CustomerComplaint?,
        ) : Configuration()
    }
}