package co.ke.xently.features.stores.presentation.list

import co.ke.xently.features.stores.presentation.utils.UiText


internal sealed interface StoreListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreListEvent

    data class ShopError(
        val error: co.ke.xently.features.shops.presentation.utils.UiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : StoreListEvent

    data class Success(val action: StoreListAction) : StoreListEvent
}
