package co.ke.xently.features.merchant.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Merchant(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
)
