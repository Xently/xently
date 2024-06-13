package co.ke.xently.features.stores.presentation.list

import androidx.compose.runtime.Stable

@Stable
data class StoreListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
)