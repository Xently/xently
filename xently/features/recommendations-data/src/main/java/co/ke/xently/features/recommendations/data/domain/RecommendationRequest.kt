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
    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + (storeDistanceMeters?.hashCode() ?: 0)
        result = 31 * result + storeCategories.hashCode()
        result = 31 * result + storeServices.hashCode()
        result = 31 * result + productCategories.hashCode()
        result = 31 * result + (maximumPrice?.hashCode() ?: 0)
        result = 31 * result + (minimumPrice?.hashCode() ?: 0)
        result = 31 * result + (locationTime?.hashCode() ?: 0)
        result = 31 * result + shoppingList.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecommendationRequest

        if (location != other.location) return false
        if (storeDistanceMeters != other.storeDistanceMeters) return false
        if (storeCategories != other.storeCategories) return false
        if (storeServices != other.storeServices) return false
        if (productCategories != other.productCategories) return false
        if (maximumPrice != other.maximumPrice) return false
        if (minimumPrice != other.minimumPrice) return false
        if (locationTime != other.locationTime) return false
        if (shoppingList != other.shoppingList) return false

        return true
    }
}