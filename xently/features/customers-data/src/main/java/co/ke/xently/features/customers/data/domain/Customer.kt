package co.ke.xently.features.customers.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    @SerialName("userId")
    val id: String,
    val name: String? = null,
    val visitCount: Int = 0,
    val placesVisitedCount: Int = 0,
    val totalPoints: Int = 0,
    val position: Int = 0,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
)
