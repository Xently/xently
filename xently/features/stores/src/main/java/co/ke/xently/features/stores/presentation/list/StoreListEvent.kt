package co.ke.xently.features.stores.presentation.list

import co.ke.xently.libraries.data.core.UiText


internal sealed interface StoreListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreListEvent

    data class Success(val action: StoreListAction) : StoreListEvent
}
