package com.kwanzatukule.features.customer.complaints.presentation.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepository
import com.kwanzatukule.features.customer.complaints.data.Filter
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.libraries.data.customer.domain.Customer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class CustomerComplaintListComponentImpl(
    customer: Customer?,
    context: ComponentContext,
    component: CustomerComplaintListComponent,
    private val repository: CustomerComplaintRepository,
) : CustomerComplaintListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(CustomerComplaintListUiState())
    override val uiState: Value<CustomerComplaintListUiState> = _uiState

    private val _event = Channel<CustomerComplaintListEvent>()
    override val event: Flow<CustomerComplaintListEvent> = _event.receiveAsFlow()

    override val customers: Flow<PagingData<CustomerComplaint>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        XentlyPagingSource { url ->
            repository.getCustomerComplaints(
                url = url,
                filter = Filter(),
            )
        }
    }.flow.cachedIn(componentScope)
}