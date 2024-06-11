package co.ke.xently.features.reviewcategory.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.DataError
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ReviewCategoryRepository {
    suspend fun save(reviewCategory: ReviewCategory): Result<Unit, DataError>
    suspend fun findAllReviewCategories(): Flow<Result<List<ReviewCategory>, DataError>>
}
