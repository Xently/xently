package co.ke.xently.features.products.presentation.edit

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ProductEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.products.data.domain.error.Error,
    ) : ProductEditDetailEvent

    data class Success(val action: ProductEditDetailAction) : ProductEditDetailEvent
}
