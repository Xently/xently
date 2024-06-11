package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import kotlinx.datetime.Month

internal sealed interface ReviewsAndFeedbackAction {
    data object FetchShopReviewSummary : ReviewsAndFeedbackAction
    data object FetchStoreReviewSummary : ReviewsAndFeedbackAction
    data object FetchReviewCategories : ReviewsAndFeedbackAction
    data object FetchStoreStatistics : ReviewsAndFeedbackAction
    class SelectReviewCategory(val category: ReviewCategory) : ReviewsAndFeedbackAction
    class SelectYear(val year: Int) : ReviewsAndFeedbackAction
    class SelectMonth(val month: Month) : ReviewsAndFeedbackAction
    class RemoveSelectedMonth(val month: Month) : ReviewsAndFeedbackAction
    class RemoveSelectedYear(val year: Int) : ReviewsAndFeedbackAction
}
