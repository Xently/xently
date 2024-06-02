package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun save(review: Review): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Review, DataError>>
}
