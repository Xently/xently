package com.kwanzatukule.features.order.presentation.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.libraries.location.tracker.domain.Location
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.order.data.Filter
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.Result
import com.kwanzatukule.features.order.presentation.utils.asUiText
import com.kwanzatukule.libraries.pagination.domain.PagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OrderListComponentImpl(
    context: ComponentContext,
    component: OrderListComponent,
    private val status: Order.Status?,
    private val repository: OrderRepository,
) : OrderListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)

        componentScope.launch {
            when (val result = repository.getMidLocation(null, Filter(status = status))) {
                is Result.Failure -> {
                    _event.send(OrderListEvent.Error(result.error.asUiText(), result.error))
                }

                is Result.Success -> {
                    result.data?.also(::setLocation)
                }
            }
        }
    }

    private val _uiState = MutableValue(OrderListUiState())
    override val uiState: Value<OrderListUiState> = _uiState

    private val _event = Channel<OrderListEvent>()
    override val event: Flow<OrderListEvent> = _event.receiveAsFlow()

    override val orders: Flow<PagingData<Order>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getOrders(
                url = url,
                filter = Filter(status = status),
            )
        }
    }.flow.cachedIn(componentScope)

    override fun setLocation(location: Location) {
        _uiState.update {
            it.copy(location = location)
        }
    }
}