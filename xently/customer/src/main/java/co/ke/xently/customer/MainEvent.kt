package co.ke.xently.customer

import co.ke.xently.features.auth.presentation.utils.UiText as AuthUiText
import co.ke.xently.features.shops.presentation.utils.UiText as ShopUiText


sealed interface MainEvent {
    data class Error(
        val error: AuthUiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : MainEvent

    data class ShopError(
        val error: ShopUiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : MainEvent

    data object Success : MainEvent
    data object SelectStore : MainEvent
}