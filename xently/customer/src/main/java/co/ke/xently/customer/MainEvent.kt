package co.ke.xently.customer

import co.ke.xently.libraries.data.core.UiText


sealed interface MainEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : MainEvent

    data class ShopError(
        val error: UiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : MainEvent

    data object Success : MainEvent
    data object SelectStore : MainEvent
}