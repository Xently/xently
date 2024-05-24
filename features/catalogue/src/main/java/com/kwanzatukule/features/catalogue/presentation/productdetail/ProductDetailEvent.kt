package com.kwanzatukule.features.catalogue.presentation.productdetail

import com.kwanzatukule.features.catalogue.presentation.UiText

sealed interface ProductDetailEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.catalogue.domain.error.Error,
    ) : ProductDetailEvent
}