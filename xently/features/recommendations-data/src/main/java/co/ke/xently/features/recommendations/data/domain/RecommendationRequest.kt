package co.ke.xently.features.recommendations.data.domain

import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.location.tracker.domain.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationRequest(
    val location: Location,
    @SerialName("radiusMeters")
    val storeDistanceMeters: Double?,
    val storeCategories: List<String>,
    val storeServices: List<String>,
    val productCategories: List<String>,
    val maximumPrice: Double?,
    val minimumPrice: Double?,
    val locationTime: Time? = Time.now(),
    val shoppingList: Set<ShoppingListItem> = emptySet(),
) : Recommendation {
    @Serializable
    data class ShoppingListItem(val name: String)
}