package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviews.presentation.utils.UiText

internal sealed interface ReviewsEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.reviews.data.domain.error.Error,
    ) : ReviewsEvent

    data object Success : ReviewsEvent
}