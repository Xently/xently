package co.ke.xently.features.shops.presentation.edit

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ShopEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : ShopEditDetailEvent

    data class Success(val action: ShopEditDetailAction) : ShopEditDetailEvent
}
