package co.ke.xently.features.stores.presentation.edit


sealed interface StoreEditDetailEvent {
    data class Error(
        val error: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreEditDetailEvent

    data object Success : StoreEditDetailEvent
}
