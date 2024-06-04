package co.ke.xently.features.products.presentation.list

import androidx.compose.runtime.Stable

@Stable
data class ProductListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
)