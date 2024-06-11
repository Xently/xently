package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun save(review: Review): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Review, DataError>>
    suspend fun findSummaryReviewForCurrentlyActiveShop(): Flow<Result<Rating, DataError>>
    suspend fun findSummaryReviewForCurrentlyActiveStore(): Flow<Result<Rating, DataError>>
    suspend fun findStoreReviewStatistics(
        category: ReviewCategory,
        filters: ReviewStatisticsFilters,
    ): Flow<Result<ReviewCategory.Statistics, DataError>>

    suspend fun getReviews(url: String?, filters: ReviewFilters): PagedResponse<Review>
}
