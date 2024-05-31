package com.kwanzatukule.features.cart.presentation.cart

import androidx.compose.runtime.Stable
import com.kwanzatukule.features.cart.domain.ShoppingCart

@Stable
data class ShoppingCartUiState(
    val shoppingCart: ShoppingCart = ShoppingCart(emptyList()),
    val isLoading: Boolean = false,
)