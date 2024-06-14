package co.ke.xently.features.reviewcategory.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory

@Stable
data class ReviewCategoryEditDetailUiState(
    val reviewCategory: ReviewCategory = ReviewCategory(),
    val name: String = reviewCategory.name,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val enableSaveButton: Boolean = !isLoading && !disableFields
}