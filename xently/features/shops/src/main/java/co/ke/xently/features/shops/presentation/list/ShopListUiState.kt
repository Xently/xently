package co.ke.xently.features.shops.presentation.list

import androidx.compose.runtime.Stable

@Stable
data class ShopListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
)