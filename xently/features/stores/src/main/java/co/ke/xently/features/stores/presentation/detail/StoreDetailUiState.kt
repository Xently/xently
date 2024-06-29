package co.ke.xently.features.stores.presentation.detail

import androidx.compose.runtime.Stable
import co.ke.xently.features.stores.data.domain.Store

@Stable
internal data class StoreDetailUiState(
    val isLoading: Boolean = false,
    val store: Store? = null,
)