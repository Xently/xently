package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviews.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.source.local.ReviewCategoryDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReviewCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ReviewCategoryDatabase,
) : ReviewCategoryRepository {
    override suspend fun save(category: ReviewCategory): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<ReviewCategory, DataError>> {
        TODO("Not yet implemented")
    }
}