package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.Error
import co.ke.xently.features.reviewcategory.presentation.utils.UiText

internal sealed interface ReviewCategoriesResponse {
    data object Loading : ReviewCategoriesResponse

    sealed interface Success : ReviewCategoriesResponse {
        data object Empty : Success
        data class NonEmpty(val data: List<ReviewCategory>) : Success
    }

    data class Failure(
        val error: UiText,
        val type: Error,
    ) : ReviewCategoriesResponse
}