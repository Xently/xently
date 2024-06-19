package co.ke.xently.features.stores.presentation.active

import androidx.compose.runtime.Stable
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.libraries.data.image.domain.Image

@Stable
data class ActiveStoreUiState(
    val store: Store? = null,
    val images: List<Image> = store?.images ?: emptyList(),
    val isShopSelected: Boolean = false,
    val isLoading: Boolean = false,
    val isImageUploading: Boolean = false,
    val canAddStore: Boolean = false,
)