package com.kwanzatukule.features.customer.complaints.presentation.list

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface CustomerComplaintListComponent {
    val customers: Flow<PagingData<CustomerComplaint>> get() = throw NotImplementedError()
    val uiState: Value<CustomerComplaintListUiState> get() = throw NotImplementedError()
    val event: Flow<CustomerComplaintListEvent> get() = flow { }
    fun onClickCustomerComplaintEntry()
    fun handleBackPress()

    data class Fake(
        val state: CustomerComplaintListUiState,
        @Suppress("PropertyName") val _customers: PagingData<CustomerComplaint>,
    ) : CustomerComplaintListComponent {
        override val uiState = MutableValue(state)
        override val customers = flowOf(_customers)
        override fun onClickCustomerComplaintEntry() {}

        override fun handleBackPress() {}
    }
}
