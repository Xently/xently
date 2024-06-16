package co.ke.xently.features.reviewcategory.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.Error
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ReviewCategoryRepository {
    suspend fun save(reviewCategory: ReviewCategory): Result<Unit, Error>
    suspend fun findAllReviewCategories(): Flow<Result<List<ReviewCategory>, Error>>
}
