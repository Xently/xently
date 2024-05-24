package com.kwanzatukule.features.cart.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kwanzatukule.features.cart.domain.ShoppingCart
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingCartItemDao {
    @Query("SELECT * FROM shopping_cart WHERE quantity > 0")
    fun getShoppingCartItems(): Flow<List<ShoppingCart.Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToShoppingCart(item: ShoppingCart.Item)

    @Query("DELETE FROM shopping_cart WHERE id = :itemId")
    suspend fun removeFromShoppingCart(itemId: Long)

    @Query("UPDATE shopping_cart SET quantity = quantity + 1 WHERE id = :itemId")
    suspend fun incrementItemQuantity(itemId: Long)

    @Query("UPDATE shopping_cart SET quantity = quantity - 1 WHERE id = :itemId")
    suspend fun decrementItemQuantity(itemId: Long)

    @Query("UPDATE shopping_cart SET quantity = :quantity WHERE id = :itemId")
    suspend fun setItemQuantity(itemId: Long, quantity: Int)

    @Query("DELETE FROM shopping_cart WHERE quantity = 0")
    suspend fun removeZeroQuantityItems()

    @Query("SELECT EXISTS (SELECT 1 FROM shopping_cart WHERE id = :productId)")
    suspend fun containsProduct(productId: Long): Boolean

    @Query("DELETE FROM shopping_cart")
    suspend fun deleteAll()
}
