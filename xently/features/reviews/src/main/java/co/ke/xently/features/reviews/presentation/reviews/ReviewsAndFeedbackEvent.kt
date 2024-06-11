package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviews.presentation.utils.UiText

internal sealed interface ReviewsAndFeedbackEvent {
    sealed interface Error<out E, out T> : ReviewsAndFeedbackEvent {
        val error: E
        val type: T

        data class ReviewsAndFeedback(
            override val error: UiText,
            override val type: co.ke.xently.features.reviews.data.domain.error.Error,
        ) : Error<UiText, co.ke.xently.features.reviews.data.domain.error.Error>

        data class ReviewCategories(
            override val error: co.ke.xently.features.reviewcategory.presentation.utils.UiText,
            override val type: co.ke.xently.features.reviewcategory.data.domain.error.Error,
        ) : Error<co.ke.xently.features.reviewcategory.presentation.utils.UiText, co.ke.xently.features.reviewcategory.data.domain.error.Error>
    }

    data object Success : ReviewsAndFeedbackEvent
}