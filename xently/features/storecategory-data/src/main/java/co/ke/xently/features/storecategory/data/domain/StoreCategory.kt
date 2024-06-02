package co.ke.xently.features.storecategory.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreCategory(
    val name: String,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StoreCategory

        return name == other.name
    }
}
