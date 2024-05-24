package com.kwanzatukule.features.cart.presentation.cart

import com.kwanzatukule.features.catalogue.presentation.UiText

sealed interface ShoppingCartEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.cart.domain.error.Error,
    ) : ShoppingCartEvent
}