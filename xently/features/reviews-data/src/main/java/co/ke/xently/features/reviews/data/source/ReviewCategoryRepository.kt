package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviews.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ReviewCategoryRepository {
    suspend fun save(category: ReviewCategory): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<ReviewCategory, DataError>>
}