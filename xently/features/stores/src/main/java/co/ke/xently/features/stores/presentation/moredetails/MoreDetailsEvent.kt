package co.ke.xently.features.stores.presentation.moredetails

import co.ke.xently.features.stores.presentation.utils.UiText

internal sealed interface MoreDetailsEvent {
    data object Success : MoreDetailsEvent
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.stores.data.domain.error.Error,
    ) : MoreDetailsEvent
}