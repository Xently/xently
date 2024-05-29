package com.kwanzatukule.features.delivery.dispatch.presentation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.delivery.dispatch.data.DispatchRepository
import com.kwanzatukule.features.delivery.dispatch.data.Filter
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.libraries.pagination.domain.PagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class DispatchListComponentImpl(
    context: ComponentContext,
    status: Dispatch.Status,
    component: DispatchListComponent,
    private val repository: DispatchRepository,
) : DispatchListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(DispatchListUiState())
    override val uiState: Value<DispatchListUiState> = _uiState

    private val _event = Channel<DispatchListEvent>()
    override val event: Flow<DispatchListEvent> = _event.receiveAsFlow()

    override val dispatches: Flow<PagingData<Dispatch>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getDispatches(
                url = url,
                filter = Filter(status = status),
            )
        }
    }.flow.cachedIn(componentScope)
}