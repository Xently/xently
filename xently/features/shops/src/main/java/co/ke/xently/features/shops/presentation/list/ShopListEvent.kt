package co.ke.xently.features.shops.presentation.list

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ShopListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : ShopListEvent

    data class Success(val action: ShopListAction) : ShopListEvent
}
