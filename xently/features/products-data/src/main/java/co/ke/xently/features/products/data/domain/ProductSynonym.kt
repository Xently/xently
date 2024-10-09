package co.ke.xently.features.products.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class ProductSynonym(val name: String) {
    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ProductSynonym

        return name == other.name
    }
}