package co.ke.xently.features.stores.presentation.active

import co.ke.xently.features.stores.presentation.utils.UiText


sealed interface ActiveStoreEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : ActiveStoreEvent

    data object Success : ActiveStoreEvent
}
