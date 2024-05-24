package com.kwanzatukule.features.cart.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import com.kwanzatukule.features.cart.domain.ShoppingCart


val LocalShoppingCartState = compositionLocalOf<State<ShoppingCart>> {
    mutableStateOf(ShoppingCart(items = emptyList()))
}