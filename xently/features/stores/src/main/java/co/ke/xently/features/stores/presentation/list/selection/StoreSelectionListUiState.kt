package co.ke.xently.features.stores.presentation.list.selection

import androidx.compose.runtime.Stable

@Stable
data class StoreSelectionListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
)