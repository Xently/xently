package co.ke.xently.features.reviews.presentation.reviewrequest

import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.features.reviews.data.domain.error.Error as ReviewError

sealed interface ReviewRequestEvent {
    class Error(
        val error: UiText,
        val type: ReviewError,
    ) : ReviewRequestEvent

    data object Success : ReviewRequestEvent
}
