package co.ke.xently.features.products.presentation.list

import co.ke.xently.features.products.presentation.utils.UiText


sealed interface ProductListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.products.data.domain.error.Error,
    ) : ProductListEvent

    data object Success : ProductListEvent
}
