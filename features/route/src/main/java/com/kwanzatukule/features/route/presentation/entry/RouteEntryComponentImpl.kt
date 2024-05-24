package com.kwanzatukule.features.route.presentation.entry

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.libraries.data.route.data.RouteRepository
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import com.kwanzatukule.libraries.data.route.domain.error.Result
import com.kwanzatukule.libraries.data.route.presentation.utils.asUiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RouteEntryComponentImpl(
    context: ComponentContext,
    component: RouteEntryComponent,
    private val repository: RouteRepository,
) : RouteEntryComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(RouteEntryUiState())
    override val uiState: Value<RouteEntryUiState> = _uiState

    private val _event = Channel<RouteEntryEvent>()
    override val event: Flow<RouteEntryEvent> = _event.receiveAsFlow()

    override fun setName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    override fun setDescription(description: String) {
        _uiState.update {
            it.copy(description = description)
        }
    }

    override fun onClickSave() {
        componentScope.launch {
            val state = _uiState.updateAndGet {
                it.copy(isLoading = true)
            }
            val route = Route(
                name = state.name,
                description = state.description,
                summary = RouteSummary(
                    bookedOrder = Random.nextInt(100),
                    variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    numberOfCustomers = Random.nextInt(100),
                    totalRouteCustomers = Random.nextInt(100),
                    geographicalDistance = Random.nextInt(1_000, 10_000),
                ),
            )
            when (val result = repository.save(route)) {
                is Result.Failure -> {
                    _event.send(RouteEntryEvent.Error(result.error.asUiText(), result.error))
                }

                is Result.Success -> {
                    handleBackPress()
                }
            }
        }.invokeOnCompletion {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    override fun onClickSaveAndAddAnother() {
        componentScope.launch {
            val state = _uiState.updateAndGet {
                it.copy(isLoading = true)
            }
            val route = Route(
                name = state.name,
                description = state.description,
                summary = RouteSummary(
                    bookedOrder = Random.nextInt(100),
                    variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    numberOfCustomers = Random.nextInt(100),
                    totalRouteCustomers = Random.nextInt(100),
                    geographicalDistance = Random.nextInt(1_000, 10_000),
                ),
            )
            when (val result = repository.save(route)) {
                is Result.Failure -> {
                    _event.send(RouteEntryEvent.Error(result.error.asUiText(), result.error))
                }

                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            name = "",
                            description = "",
                        )
                    }
                }
            }
        }.invokeOnCompletion {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}