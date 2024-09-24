package co.ke.xently.features.stores.presentation.active

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ActiveStoreEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : ActiveStoreEvent

    data class Success(
        val action: ActiveStoreAction,
    ) : ActiveStoreEvent

    data object SelectShop : ActiveStoreEvent
    data object SelectStore : ActiveStoreEvent
}
