package co.ke.xently.features.productcategory.data.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ProductCategory(
    val name: String,
    val isMain: Boolean = false,
    @Transient
    val selected: Boolean = false,
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

        other as ProductCategory

        return name == other.name
    }
}
