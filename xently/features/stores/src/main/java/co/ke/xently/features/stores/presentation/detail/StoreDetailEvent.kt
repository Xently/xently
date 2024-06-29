package co.ke.xently.features.stores.presentation.detail

import co.ke.xently.features.stores.presentation.utils.UiText

internal sealed interface StoreDetailEvent {
    data object Success : StoreDetailEvent
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreDetailEvent
}