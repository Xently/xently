package co.ke.xently.features.stores.data.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.location.tracker.domain.Location
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Store(
    val categories: List<StoreCategory> = emptyList(),
    val distance: Double? = null,
    val email: String? = null,
    val id: Long = -1,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
    val location: Location = Location(Double.NaN, Double.NaN),
    val name: String = "",
    val services: List<StoreService> = emptyList(),
    val paymentMethods: List<StorePaymentMethod> = emptyList(),
    val images: List<UploadResponse> = emptyList(),
    val description: String? = null,
    val slug: String = "",
    val telephone: String? = null,
    val shop: Shop = Shop(name = ""),
    val openingHours: List<OpeningHour> = emptyList(),
    @Transient
    val isActivated: Boolean = false,
) {
    override fun toString(): String {
        return name
    }

    companion object {
        val DEFAULT = Store(
            name = "Branch name goes here",
            shop = Shop(
                name = "Xently Shop",
                onlineShopUrl = "https://picsum.photos/id/237/100/100",
                id = 1,
                links = mapOf("add-store" to Link(href = "https://picsum.photos/id/237/100/100")),
            ),
            description = "Short description about the business/hotel will go here.",
            services = List(3) {
                StoreService(name = "Service ${it + 1}")
            },
            categories = List(3) {
                StoreCategory(name = "Category ${it + 1}")
            },
            images = List(1) {
                UploadResponse(
                    links = mapOf(
                        "media" to Link(
                            href = "https://picsum.photos/id/237/${100 * (it + 1)}/${100 * (it + 1)}",
                        ),
                    ),
                )
            },
            openingHours = DayOfWeek.entries.map {
                OpeningHour(
                    dayOfWeek = it,
                    openTime = Time(7, 0),
                    closeTime = Time(17, 0),
                    open = it.isoDayNumber !in setOf(6, 7),
                )
            },
        )
    }
}
