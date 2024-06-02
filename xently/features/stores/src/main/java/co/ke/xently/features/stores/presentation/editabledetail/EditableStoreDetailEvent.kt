package co.ke.xently.features.stores.presentation.editabledetail

import co.ke.xently.features.stores.presentation.utils.UiText


sealed interface EditableStoreDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : EditableStoreDetailEvent

    data object Success : EditableStoreDetailEvent
}
