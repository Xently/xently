package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.libraries.data.core.UiText

internal sealed interface ReviewSummaryResponse {
    data object Loading : ReviewSummaryResponse

    data class Success(val data: Rating) : ReviewSummaryResponse

    data class Failure(
        val error: UiText,
        val type: co.ke.xently.features.reviews.data.domain.error.Error,
    ) : ReviewSummaryResponse
}