package co.ke.xently.features.reviews.presentation.comments

import co.ke.xently.features.reviews.domain.Star

internal sealed interface ReviewCommentListAction {
    class SelectStarRating(val star: Star) : ReviewCommentListAction
    class RemoveStarRating(val star: Star) : ReviewCommentListAction
    class ChangeQuery(val query: String) : ReviewCommentListAction
    class Search(val query: String) : ReviewCommentListAction
}