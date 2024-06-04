package co.ke.xently.features.stores.presentation.editabledetail

import androidx.compose.runtime.Stable
import co.ke.xently.features.stores.data.domain.Store

@Stable
data class EditableStoreDetailUiState(
    val store: Store? = null,
    val isShopSelected: Boolean = false,
    val isLoading: Boolean = false,
    val isImageUploading: Boolean = false,
    val canAddStore: Boolean = false,
)