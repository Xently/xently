package co.ke.xently.features.products.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import kotlinx.serialization.Serializable

@Serializable
internal data class ProductSaveRequest(
    val unitPrice: Double,
    val name: String,
    val packCount: Int,
    val categories: Set<ProductCategory>,
    val description: String?,
)