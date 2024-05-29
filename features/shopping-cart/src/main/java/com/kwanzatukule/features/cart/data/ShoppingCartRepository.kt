package com.kwanzatukule.features.cart.data

import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.data.ShoppingCartChecker
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse
import kotlinx.coroutines.flow.Flow

interface ShoppingCartRepository : ShoppingCartChecker {
    fun getShoppingCart(): Flow<ShoppingCart>
    suspend fun clearShoppingCart()
    suspend fun addToOrRemoveFromShoppingCart(product: Product)
    suspend fun incrementShoppingCartQuantity(product: Product)
    suspend fun decrementShoppingCartQuantity(product: Product)
    suspend fun setShoppingCartQuantity(product: Product, quantity: Int)
    suspend fun getShoppingList(url: String?): PagedResponse<ShoppingCart.Item>
}