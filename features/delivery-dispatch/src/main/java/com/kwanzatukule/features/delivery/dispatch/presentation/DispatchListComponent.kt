package com.kwanzatukule.features.delivery.dispatch.presentation

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface DispatchListComponent {
    val dispatches: Flow<PagingData<Dispatch>> get() = throw NotImplementedError()

    val uiState: Value<DispatchListUiState> get() = throw NotImplementedError()
    val event: Flow<DispatchListEvent> get() = flow { }
    fun onClickViewRoute(dispatch: Dispatch)
    fun onClickViewOrders(dispatch: Dispatch)

    data class Fake(
        val state: DispatchListUiState,
        @Suppress("PropertyName") val _dispatches: PagingData<Dispatch>,
    ) : DispatchListComponent {
        override val uiState = MutableValue(state)
        override val dispatches = flowOf(_dispatches)
        override fun onClickViewRoute(dispatch: Dispatch) {}
        override fun onClickViewOrders(dispatch: Dispatch) {}
    }
}
