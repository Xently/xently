package co.ke.xently.features.shops.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Shop(
    val name: String,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
    val onlineShopUrl: String? = null,
    val slug: String = name.lowercase().replace(' ', '-'),
    val id: Long = -1,
    @Transient
    val isActivated: Boolean = false,
) {
    override fun toString(): String {
        return name
    }

    companion object {
        val DEFAULT = Shop(
            id = 1L,
            name = "Shop name",
            slug = "shop-name",
            onlineShopUrl = "https://example.com",
            links = mapOf(
                "self" to Link(href = "https://example.com"),
                "add-store" to Link(href = "https://example.com/edit"),
            ),
        )
    }
}
