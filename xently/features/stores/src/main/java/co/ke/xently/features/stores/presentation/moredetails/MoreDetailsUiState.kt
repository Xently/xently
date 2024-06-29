package co.ke.xently.features.stores.presentation.moredetails

import androidx.compose.runtime.Stable
import co.ke.xently.features.stores.data.domain.Store

@Stable
internal data class MoreDetailsUiState(
    val isLoading: Boolean = false,
    val store: Store? = null,
)