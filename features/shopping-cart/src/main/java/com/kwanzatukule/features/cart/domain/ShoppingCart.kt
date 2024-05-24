package com.kwanzatukule.features.cart.domain

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class ShoppingCart(
    val items: List<Item>,
    val numberOfItems: Int = items.size,
    val totalPrice: Int = items.sumOf { it.totalPrice },
) {
    fun hasItem(product: Product): Boolean {
        return product.id in items.map { it.id }
    }

    @Serializable
    @Entity(tableName = "shopping_cart")
    @Stable
    data class Item(
        val product: Product,
        val quantity: Int,
        @PrimaryKey
        val id: Long = product.id,
    ) {
        @Stable
        val totalPrice: Int
            get() = product.price * quantity
    }
}
