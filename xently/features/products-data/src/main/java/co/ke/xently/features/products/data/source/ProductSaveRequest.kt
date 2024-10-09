package co.ke.xently.features.products.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.data.domain.ProductSynonym
import kotlinx.serialization.Serializable

@Serializable
internal data class ProductSaveRequest(
    val unitPrice: Double,
    val name: String,
    val packCount: Int,
    val synonyms: Set<ProductSynonym>,
    val categories: Set<ProductCategory>,
    val description: String?,
)