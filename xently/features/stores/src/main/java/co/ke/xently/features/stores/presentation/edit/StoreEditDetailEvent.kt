package co.ke.xently.features.stores.presentation.edit


internal sealed interface StoreEditDetailEvent {
    data class Error(
        val error: co.ke.xently.features.stores.data.domain.error.Error,
    ) : StoreEditDetailEvent

    data class Success(val action: StoreEditDetailAction) : StoreEditDetailEvent
}
