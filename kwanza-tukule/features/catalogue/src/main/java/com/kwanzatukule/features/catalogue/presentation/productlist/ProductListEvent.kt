package com.kwanzatukule.features.catalogue.presentation.productlist

import com.kwanzatukule.features.catalogue.presentation.UiText

sealed interface ProductListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.catalogue.domain.error.Error,
    ) : ProductListEvent
}