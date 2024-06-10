package co.ke.xently.features.products.presentation.list

import co.ke.xently.features.products.presentation.utils.UiText


internal sealed interface ProductListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.products.data.domain.error.Error,
    ) : ProductListEvent

    data class Success(val action: ProductListAction) : ProductListEvent
}
