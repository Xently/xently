package co.ke.xently.features.products.presentation.edit

import co.ke.xently.features.products.presentation.utils.UiText


sealed interface ProductEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.products.data.domain.error.Error,
    ) : ProductEditDetailEvent

    data object Success : ProductEditDetailEvent
}
