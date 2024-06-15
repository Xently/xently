package co.ke.xently.business.landing.presentation

import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.libraries.data.auth.CurrentUser

data class LandingUiState(
    val canAddShop: Boolean = false,
    val isSignOutInProgress: Boolean = false,
    val user: CurrentUser? = null,
    val shops: List<Shop> = emptyList(),
)