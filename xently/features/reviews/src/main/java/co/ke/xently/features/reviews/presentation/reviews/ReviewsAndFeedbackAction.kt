package co.ke.xently.features.reviews.presentation.reviews

import android.content.Context
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import kotlinx.datetime.Month

internal sealed interface ReviewsAndFeedbackAction {
    data object FetchShopReviewSummary : ReviewsAndFeedbackAction
    data object FetchStoreReviewSummary : ReviewsAndFeedbackAction
    data object FetchReviewCategories : ReviewsAndFeedbackAction
    class FetchStoreStatistics(val context: Context) : ReviewsAndFeedbackAction
    class SelectReviewCategory(
        val context: Context,
        val category: ReviewCategory,
    ) : ReviewsAndFeedbackAction
    class SelectYear(val year: Int) : ReviewsAndFeedbackAction
    class SelectMonth(val month: Month) : ReviewsAndFeedbackAction
    data object RemoveSelectedMonth : ReviewsAndFeedbackAction
    data object RemoveSelectedYear : ReviewsAndFeedbackAction
    class Refresh(val context: Context) : ReviewsAndFeedbackAction
}
