package co.ke.xently.features.reviews.presentation.reviewrequest

internal sealed interface ReviewRequestAction {
    data class ChangeMessage(
        val categoryName: String,
        val message: String,
    ) : ReviewRequestAction

    data class RequestMessageEdit(
        val categoryName: String,
    ) : ReviewRequestAction

    data class PostRating(
        val categoryName: String,
        val url: String,
        val message: String?,
    ) : ReviewRequestAction
}