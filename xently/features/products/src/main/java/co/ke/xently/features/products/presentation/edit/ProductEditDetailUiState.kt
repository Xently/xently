package co.ke.xently.features.products.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.products.data.domain.Product

@Stable
data class ProductEditDetailUiState(
    val categoryName: String = "",
    val product: Product = Product(),
    val name: String = product.name,
    val unitPrice: String = product.unitPrice.toString(),
    val description: String = product.description ?: "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
)