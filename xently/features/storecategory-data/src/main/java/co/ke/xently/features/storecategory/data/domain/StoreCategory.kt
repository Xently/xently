package co.ke.xently.features.storecategory.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class StoreCategory(
    val name: String,
    val isMain: Boolean = false,
    @Transient
    val selected: Boolean = false,
) : Parcelable {
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
