package co.ke.xently.features.recommendations.data.domain

import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.stores.data.domain.Store
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationResponse(
    val estimatedExpenditure: EstimatedExpenditure,
    val store: Store,
    val hit: Hit,
    val miss: Miss,
) {
    @Serializable
    data class EstimatedExpenditure(val unit: Double = 0.0, val total: Double = 0.0)

    @Serializable
    data class Miss(val count: Int, val items: List<Item>) {
        @Serializable
        data class Item(val value: String)
    }

    @Serializable
    data class Hit(val count: Int, val items: List<Item>) {
        @Serializable
        data class Item(
            val bestMatched: Product,
            val shoppingList: ShoppingList,
        ) {
            @Serializable
            data class ShoppingList(val name: String, val quantityToPurchase: Int = 1)
        }
    }
}