package co.ke.xently.business.landing.presentation

import co.ke.xently.features.shops.data.domain.Shop

data class LandingUiState(
    val canAddShop: Boolean = false,
    val shops: List<Shop> = emptyList(),
)