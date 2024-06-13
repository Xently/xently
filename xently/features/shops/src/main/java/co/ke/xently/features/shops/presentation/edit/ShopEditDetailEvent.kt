package co.ke.xently.features.shops.presentation.edit

import co.ke.xently.features.shops.presentation.utils.UiText


sealed interface ShopEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : ShopEditDetailEvent

    data object Success : ShopEditDetailEvent
}
