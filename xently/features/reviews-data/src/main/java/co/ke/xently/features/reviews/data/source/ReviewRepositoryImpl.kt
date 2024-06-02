package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReviewRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ReviewDatabase,
) : ReviewRepository {
    override suspend fun save(review: Review): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Review, DataError>> {
        TODO("Not yet implemented")
    }
}