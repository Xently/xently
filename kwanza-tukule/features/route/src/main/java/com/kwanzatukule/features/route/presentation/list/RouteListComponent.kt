package com.kwanzatukule.features.route.presentation.list

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface RouteListComponent {
    val routes: Flow<PagingData<Route>> get() = throw NotImplementedError()
    val uiState: Value<RouteListUiState> get() = throw NotImplementedError()
    val event: Flow<RouteListEvent> get() = flow { }
    fun onClickRouteEntry()
    fun onClickRoute(route: Route)

    data class Fake(
        val state: RouteListUiState,
        @Suppress("PropertyName") val _routes: PagingData<Route>,
    ) : RouteListComponent {
        override val uiState = MutableValue(state)
        override val routes = flowOf(_routes)
        override fun onClickRouteEntry() {}
        override fun onClickRoute(route: Route) {}
    }
}
