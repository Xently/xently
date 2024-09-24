package co.ke.xently.features.stores.presentation.list.selection

import co.ke.xently.libraries.data.core.UiText


internal sealed interface StoreSelectionListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreSelectionListEvent

    data class Success(val action: StoreSelectionListAction) : StoreSelectionListEvent
}
