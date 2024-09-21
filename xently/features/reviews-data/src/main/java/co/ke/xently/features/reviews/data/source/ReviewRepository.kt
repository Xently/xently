package co.ke.xently.features.reviews.data.source

import androidx.paging.PagingData
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.Error
import co.ke.xently.features.reviews.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun postRating(url: String, message: String?): Result<Unit, Error>
    suspend fun syncWithServer()
    fun findSummaryReviewForCurrentlyActiveShop(): Flow<Result<Rating, Error>>
    fun findSummaryReviewForCurrentlyActiveStore(): Flow<Result<Rating, Error>>
    fun findStoreReviewStatistics(
        category: ReviewCategory,
        filters: ReviewStatisticsFilters,
    ): Flow<Result<ReviewCategory.Statistics, Error>>

    fun getReviews(url: String, filters: ReviewFilters): Flow<PagingData<Review>>
}
