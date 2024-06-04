package co.ke.xently.features.stores.presentation.edit

import co.ke.xently.features.stores.presentation.utils.UiText


sealed interface StoreEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreEditDetailEvent

    data object Success : StoreEditDetailEvent
}
