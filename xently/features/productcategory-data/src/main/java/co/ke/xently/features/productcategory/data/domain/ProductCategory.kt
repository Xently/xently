package co.ke.xently.features.productcategory.data.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ProductCategory(
    val name: String,
    val isMain: Boolean = false,
    @Transient
    val selected: Boolean = false,
)
