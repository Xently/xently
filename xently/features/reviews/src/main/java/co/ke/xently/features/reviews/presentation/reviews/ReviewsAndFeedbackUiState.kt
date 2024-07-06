package co.ke.xently.features.reviews.presentation.reviews

import androidx.compose.runtime.Stable
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters

@Stable
internal data class ReviewsAndFeedbackUiState(
    val selectedCategory: ReviewCategory? = null,
    val statisticsResponse: StatisticsResponse? = null,
    val shopReviewSummaryResponse: ReviewSummaryResponse = ReviewSummaryResponse.Loading,
    val storeReviewSummaryResponse: ReviewSummaryResponse = ReviewSummaryResponse.Loading,
    val categoriesResponse: ReviewCategoriesResponse = ReviewCategoriesResponse.Loading,
    @Stable
    val selectedFilters: ReviewStatisticsFilters = ReviewStatisticsFilters(),
)
