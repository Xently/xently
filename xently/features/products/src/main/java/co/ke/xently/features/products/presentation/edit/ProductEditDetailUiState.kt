package co.ke.xently.features.products.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.DescriptionError
import co.ke.xently.features.products.data.domain.error.NameError
import co.ke.xently.features.products.data.domain.error.PriceError
import co.ke.xently.libraries.data.image.domain.File

@Stable
data class ProductEditDetailUiState(
    val categoryName: String = "",
    val product: Product = Product(),
    val name: String = product.name,
    val nameError: NameError? = null,
    val unitPrice: String = product.unitPrice.toString()
        .removeSuffix(".0")
        .takeIf { it != "0" }
        ?: "",
    val unitPriceError: PriceError? = null,
    val description: String = product.description ?: "",
    val descriptionError: DescriptionError? = null,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
    val images: List<File?> = (product.images + List(5) { null }).take(5),
) {
    val enableSaveButton: Boolean = !isLoading && !disableFields
    val isFormValid: Boolean = nameError == null
            && unitPriceError == null
            && descriptionError == null
}