package com.kwanzatukule.features.customer.presentation.entry

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.libraries.data.customer.data.CustomerRepository
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.customer.domain.error.Result
import com.kwanzatukule.libraries.data.customer.presentation.utils.asUiText
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.location.tracker.domain.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CustomerEntryComponentImpl(
    route: Route,
    context: ComponentContext,
    component: CustomerEntryComponent,
    private val repository: CustomerRepository,
) : CustomerEntryComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(CustomerEntryUiState(route = route))
    override val uiState: Value<CustomerEntryUiState> = _uiState

    private val _event = Channel<CustomerEntryEvent>()
    override val event: Flow<CustomerEntryEvent> = _event.receiveAsFlow()

    override fun setName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    override fun setEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    override fun setPhone(phone: String) {
        _uiState.update {
            it.copy(phone = phone)
        }
    }

    override fun setLocation(location: Location) {
        _uiState.update {
            it.copy(location = location)
        }
    }

    override fun onClickSave() {
        save(::handleBackPress)
    }

    override fun onClickSaveAndAddAnother() {
        save {
            _uiState.update {
                it.copy(
                    name = "",
                    email = "",
                    phone = "",
                )
            }
        }
    }

    private inline fun save(crossinline onSuccess: () -> Unit) {
        componentScope.launch {
            val state = _uiState.updateAndGet {
                it.copy(isLoading = true)
            }
            val customer = Customer(
                name = state.name,
                email = state.email,
                phone = state.phone,
            )
            when (val result = repository.save(customer)) {
                is Result.Failure -> {
                    _event.send(CustomerEntryEvent.Error(result.error.asUiText(), result.error))
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