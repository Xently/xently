package com.kwanzatukule.features.customer.presentation.list

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.libraries.data.customer.domain.Customer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface CustomerListComponent {
    val customers: Flow<PagingData<Customer>> get() = throw NotImplementedError()
    val uiState: Value<CustomerListUiState> get() = throw NotImplementedError()
    val event: Flow<CustomerListEvent> get() = flow { }
    fun onClickCustomerEntry()
    fun handleBackPress()
    fun onClickCustomer(customer: Customer)

    data class Fake(
        val state: CustomerListUiState,
        @Suppress("PropertyName") val _customers: PagingData<Customer>,
    ) : CustomerListComponent {
        override val uiState = MutableValue(state)
        override val customers = flowOf(_customers)
        override fun onClickCustomerEntry() {}
        override fun handleBackPress() {}
        override fun onClickCustomer(customer: Customer) {}
    }
}
