package com.kwanzatukule.features.customer.presentation.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.libraries.pagination.domain.PagingSource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.libraries.data.customer.data.CustomerRepository
import com.kwanzatukule.libraries.data.customer.data.Filter
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class CustomerListComponentImpl(
    route: Route,
    context: ComponentContext,
    component: CustomerListComponent,
    private val repository: CustomerRepository,
) : CustomerListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(CustomerListUiState(route = route))
    override val uiState: Value<CustomerListUiState> = _uiState

    private val _event = Channel<CustomerListEvent>()
    override val event: Flow<CustomerListEvent> = _event.receiveAsFlow()

    override val customers: Flow<PagingData<Customer>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getCustomers(
                url = url,
                filter = Filter(),
            )
        }
    }.flow.cachedIn(componentScope)
}