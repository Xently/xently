package com.kwanzatukule.features.customer.complaints.presentation.entry

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CustomerComplaintEntryComponent {
    val uiState: Value<CustomerComplaintEntryUiState> get() = throw NotImplementedError()
    val event: Flow<CustomerComplaintEntryEvent> get() = flow { }
    fun handleBackPress()
    fun setName(name: String) {}
    fun setEmail(email: String) {}
    fun setPhone(phone: String) {}
    fun onClickSave() {}
    fun onClickSaveAndAddAnother() {}

    data class Fake(val state: CustomerComplaintEntryUiState) : CustomerComplaintEntryComponent {
        override val uiState = MutableValue(state)
        override fun handleBackPress() {}
    }
}