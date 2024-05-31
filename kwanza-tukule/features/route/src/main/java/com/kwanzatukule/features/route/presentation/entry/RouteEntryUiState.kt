package com.kwanzatukule.features.route.presentation.entry

import androidx.compose.runtime.Stable

@Stable
data class RouteEntryUiState(
    val name: String = "",
    val description: String = "",
    val query: String = "",
    val isLoading: Boolean = false,
) {
    val enableSaveButton: Boolean
        get() = name.isNotBlank()
                && description.isNotBlank()
                && !isLoading
}
