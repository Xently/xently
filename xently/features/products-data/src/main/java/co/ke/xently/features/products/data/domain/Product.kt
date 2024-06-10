package co.ke.xently.features.products.data.domain

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.image.domain.ImageResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("images")
    val images: List<ImageResponse> = emptyList(),
    @SerialName("name")
    val name: String = "",
    @SerialName("descriptiveName")
    val descriptiveName: String = "",
    @SerialName("id")
    val id: Long = -1,
    @SerialName("packCount")
    val packCount: Int = 1,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("categories")
    val categories: List<ProductCategory> = emptyList(),
    @SerialName("unitPrice")
    val unitPrice: Double = 0.0,
    @SerialName("description")
    val description: String? = null,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    override fun toString(): String {
        return descriptiveName.ifBlank { name }
    }
}
