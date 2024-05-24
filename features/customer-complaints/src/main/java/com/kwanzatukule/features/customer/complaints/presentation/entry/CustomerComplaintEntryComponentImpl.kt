package com.kwanzatukule.features.customer.complaints.presentation.entry

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepository
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.features.customer.complaints.domain.error.Result
import com.kwanzatukule.features.customer.complaints.presentation.utils.asUiText
import com.kwanzatukule.libraries.data.customer.domain.Customer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CustomerComplaintEntryComponentImpl(
    customer: Customer?,
    context: ComponentContext,
    component: CustomerComplaintEntryComponent,
    private val repository: CustomerComplaintRepository,
) : CustomerComplaintEntryComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(CustomerComplaintEntryUiState())
    override val uiState: Value<CustomerComplaintEntryUiState> = _uiState

    private val _event = Channel<CustomerComplaintEntryEvent>()
    override val event: Flow<CustomerComplaintEntryEvent> = _event.receiveAsFlow()

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
            val customer = CustomerComplaint(
                name = state.name,
                email = state.email,
                phone = state.phone,
            )
            when (val result = repository.save(customer)) {
                is Result.Failure -> {
                    _event.send(
                        CustomerComplaintEntryEvent.Error(
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