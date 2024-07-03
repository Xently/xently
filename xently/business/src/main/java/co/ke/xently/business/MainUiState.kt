package co.ke.xently.business

import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.libraries.data.auth.CurrentUser

data class MainUiState(
    val canAddShop: Boolean = false,
    val isLoading: Boolean = false,
    val user: CurrentUser? = null,
    val shops: List<Shop> = emptyList(),
)