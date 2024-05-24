package com.kwanzatukule.features.route.presentation.entry

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RouteEntryComponent {
    val uiState: Value<RouteEntryUiState> get() = throw NotImplementedError()
    val event: Flow<RouteEntryEvent> get() = flow { }
    fun handleBackPress()
    fun setName(name: String) {}
    fun setDescription(description: String) {}
    fun onClickSave() {}
    fun onClickSaveAndAddAnother() {}

    data class Fake(val state: RouteEntryUiState) : RouteEntryComponent {
        override val uiState = MutableValue(state)
        override fun handleBackPress() {}
    }
}