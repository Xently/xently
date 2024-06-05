package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters


internal data class ReviewsUiState(
    val shopRating: Rating?,
    val storeRating: Rating?,
    val categories: List<ReviewCategory>,
    val selectedCategory: ReviewCategory? = null,
    val statisticsResponse: StatisticsResponse? = null,
    val selectedFilters: ReviewStatisticsFilters = ReviewStatisticsFilters(),
    val isLoading: Boolean = false,
)
