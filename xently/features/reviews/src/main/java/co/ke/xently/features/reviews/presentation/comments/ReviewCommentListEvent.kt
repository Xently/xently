package co.ke.xently.features.reviews.presentation.comments

import co.ke.xently.features.reviews.presentation.utils.UiText


internal sealed interface ReviewCommentListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.reviews.data.domain.error.Error,
    ) : ReviewCommentListEvent

    data class Success(val action: ReviewCommentListAction) : ReviewCommentListEvent
}
