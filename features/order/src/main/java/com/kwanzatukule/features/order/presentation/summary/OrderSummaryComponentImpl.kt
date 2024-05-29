package com.kwanzatukule.features.order.presentation.summary

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.Result
import com.kwanzatukule.features.order.presentation.utils.asUiText
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class OrderSummaryComponentImpl(
    customer: Customer,
    route: Route,
    context: ComponentContext,
    component: OrderSummaryComponent,
    private val repository: OrderRepository,
) : OrderSummaryComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState =
        MutableValue(OrderSummaryUiState(order = Order(customer = customer, route = route)))
    override val uiState: Value<OrderSummaryUiState> = _uiState

    private val _event = Channel<OrderSummaryEvent>()
    override val event: Flow<OrderSummaryEvent> = _event.receiveAsFlow()

    override fun onClickPlaceOrder() {
        save(::onOrderPlaced)
    }

    private inline fun save(crossinline onSuccess: () -> Unit) {
        componentScope.launch {
            val state = _uiState.updateAndGet {
                it.copy(isLoading = true)
            }
            when (val result = repository.placeOrder(state.order)) {
                is Result.Failure -> {
                    _event.send(
                        OrderSummaryEvent.Error(
                            result.error.asUiText(),
                            result.error
                        )
                    )
                }

                is Result.Success -> {
                    onSuccess()
                }
            }
        }.invokeOnCompletion {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}