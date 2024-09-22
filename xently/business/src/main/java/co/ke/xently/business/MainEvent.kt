package co.ke.xently.business


sealed interface MainEvent {
    data class Error(
        val error: AuthUiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : MainEvent

    data class ShopError(
        val error: ShopUiText,
        val type: co.ke.xently.features.shops.data.domain.error.Error,
    ) : MainEvent

    data object Success : MainEvent
    data object SelectStore : MainEvent
}