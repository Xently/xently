package com.kwanzatukule.features.cart.data

import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ShoppingCartRepositoryImpl @Inject constructor(
    private val database: ShoppingCartDatabase,
) : ShoppingCartRepository {
    override fun getShoppingCart(): Flow<ShoppingCart> {
        return database.shoppingCartItemDao()
            .getShoppingCartItems()
            .map { ShoppingCart(items = it) }
    }

    override suspend fun clearShoppingCart() {
        database.shoppingCartItemDao().deleteAll()
    }

    override suspend fun addToOrRemoveFromShoppingCart(product: Product) {
        withContext(Dispatchers.IO) {
            if (product.inShoppingCart) {
                val item = ShoppingCart.Item(
                    product = product,
                    quantity = 1,
                )
                database.shoppingCartItemDao()
                    .addToShoppingCart(item = item)
            } else {
                database.shoppingCartItemDao()
                    .removeFromShoppingCart(itemId = product.id)
            }
        }
    }

    override suspend fun incrementShoppingCartQuantity(product: Product) {
        withContext(Dispatchers.IO) {
            database.shoppingCartItemDao()
                .incrementItemQuantity(product.id)
        }
    }

    override suspend fun decrementShoppingCartQuantity(product: Product) {
        database.withTransactionFacade {
            database.shoppingCartItemDao()
                .decrementItemQuantity(itemId = product.id)
            database.shoppingCartItemDao()
                .removeZeroQuantityItems()
        }
    }

    override suspend fun setShoppingCartQuantity(product: Product, quantity: Int) {
        database.withTransactionFacade {
            database.shoppingCartItemDao().setItemQuantity(
                itemId = product.id,
                quantity = quantity,
            )
            database.shoppingCartItemDao()
                .removeZeroQuantityItems()
        }
    }

    override suspend fun getShoppingList(url: String?): PagedResponse<ShoppingCart.Item> {
        val items = List(Random.nextInt(3, 20)) {
            ShoppingCart.Item(
                product = Product(
                    id = (it + 1).toLong(),
                    name = "Random product name",
                    price = Random.nextInt(200, 10_500),
                    image = "https://picsum.photos/200/300",
                ),
                quantity = Random.nextInt(1, 10),
            )
        }
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to items))
    }

    override suspend fun containsProduct(product: Product): Boolean {
        return database.shoppingCartItemDao().containsProduct(product.id)
    }
}