package com.kwanzatukule.features.route.presentation.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.libraries.pagination.data.PagingSource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.libraries.data.route.data.Filter
import com.kwanzatukule.libraries.data.route.data.RouteRepository
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class RouteListComponentImpl(
    context: ComponentContext,
    component: RouteListComponent,
    private val repository: RouteRepository,
) : RouteListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(RouteListUiState())
    override val uiState: Value<RouteListUiState> = _uiState

    private val _event = Channel<RouteListEvent>()
    override val event: Flow<RouteListEvent> = _event.receiveAsFlow()

    override val routes: Flow<PagingData<Route>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getRoutes(
                url = url,
                filter = Filter(),
            )
        }
    }.flow.cachedIn(componentScope)
}