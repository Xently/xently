package co.ke.xently.features.stores.data.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.image.domain.ImageResponse
import co.ke.xently.libraries.location.tracker.domain.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    @SerialName("categories")
    val categories: List<StoreCategory> = emptyList(),
    @SerialName("distance")
    val distance: Double? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("id")
    val id: Long = -1,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
    @SerialName("location")
    val location: Location = Location(Double.NaN, Double.NaN),
    @SerialName("name")
    val name: String = "",
    @SerialName("services")
    val services: List<StoreService> = emptyList(),
    @SerialName("images")
    val images: List<ImageResponse> = emptyList(),
    @SerialName("description")
    val description: String? = null,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("telephone")
    val telephone: String? = null,
    val shop: Shop = Shop(name = ""),
    val openingHours: List<OpeningHour> = emptyList(),
)
