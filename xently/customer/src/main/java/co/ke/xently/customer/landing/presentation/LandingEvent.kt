package co.ke.xently.customer.landing.presentation

import co.ke.xently.features.auth.presentation.utils.UiText as AuthUiText
import co.ke.xently.features.shops.presentation.utils.UiText as ShopUiText


sealed interface LandingEvent {
    data class Error(
        val error: AuthUiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : LandingEvent

    data class ShopError(
        val error: ShopUiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : LandingEvent

    data object Success : LandingEvent
    data object SelectStore : LandingEvent
}