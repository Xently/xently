package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory

internal sealed interface ReviewsAction {
    class SelectReviewCategory(val category: ReviewCategory) : ReviewsAction
}
