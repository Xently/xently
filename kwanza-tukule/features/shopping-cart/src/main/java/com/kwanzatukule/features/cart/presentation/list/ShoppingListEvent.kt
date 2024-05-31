package com.kwanzatukule.features.cart.presentation.list

import com.kwanzatukule.features.cart.presentation.utils.UiText

sealed interface ShoppingListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.cart.domain.error.Error,
    ) : ShoppingListEvent
}