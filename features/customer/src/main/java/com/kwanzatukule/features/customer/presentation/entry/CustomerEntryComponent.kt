package com.kwanzatukule.features.customer.presentation.entry

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.libraries.location.tracker.domain.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CustomerEntryComponent {
    val uiState: Value<CustomerEntryUiState> get() = throw NotImplementedError()
    val event: Flow<CustomerEntryEvent> get() = flow { }
    fun handleBackPress()
    fun setName(name: String) {}
    fun setEmail(email: String) {}
    fun setPhone(phone: String) {}
    fun setLocation(location: Location) {}
    fun onClickSave() {}
    fun onClickSaveAndAddAnother() {}

    data class Fake(val state: CustomerEntryUiState) : CustomerEntryComponent {
        override val uiState = MutableValue(state)
        override fun handleBackPress() {}
    }
}