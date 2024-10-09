package co.ke.xently.features.products.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.LocalFieldError
import co.ke.xently.libraries.data.image.domain.File
import com.dokar.chiptextfield.Chip

@Stable
data class ProductEditDetailUiState(
    val categoryName: String = "",
    val product: Product = Product(),
    val name: String = product.name,
    val nameError: List<LocalFieldError>? = null,
    val unitPrice: String = product.unitPrice.toString()
        .removeSuffix(".0")
        .takeIf { it != "0" }
        ?: "",
    val unitPriceError: List<LocalFieldError>? = null,
    val description: String = product.description ?: "",
    val descriptionError: List<LocalFieldError>? = null,
    val synonyms: List<Chip> = product.synonyms.map { Chip(it.name) },
    val additionalCategories: List<Chip> = product.categories.filter { !it.isMain }
        .map { Chip(it.name) },
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
    val images: List<File?> = (product.images + List(5) { null }).take(5),
) {
    val enableSaveButton: Boolean = !isLoading && !disableFields
    val isFormValid: Boolean = nameError.isNullOrEmpty()
            && unitPriceError.isNullOrEmpty()
            && descriptionError.isNullOrEmpty()
}