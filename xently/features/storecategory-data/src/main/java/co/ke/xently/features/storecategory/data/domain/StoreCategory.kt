package co.ke.xently.features.storecategory.data.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StoreCategory(
    val name: String,
    val isMain: Boolean = false,
    @Transient
    val selected: Boolean = false,
)
