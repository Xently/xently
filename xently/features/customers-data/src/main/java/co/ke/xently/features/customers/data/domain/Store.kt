package co.ke.xently.features.customers.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Long,
    val name: String,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
)
