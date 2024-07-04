package co.ke.xently.features.recommendations.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItem(val name: String) {
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingListItem

        return name == other.name
    }
}