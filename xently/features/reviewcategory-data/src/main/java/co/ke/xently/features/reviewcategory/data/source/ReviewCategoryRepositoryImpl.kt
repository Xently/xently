package co.ke.xently.features.reviewcategory.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.Error
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import co.ke.xently.features.reviewcategory.data.domain.error.toReviewCategoryError
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryDatabase
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryEntity
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Singleton
internal class ReviewCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ReviewCategoryDatabase,
) : ReviewCategoryRepository {
    override suspend fun save(reviewCategory: ReviewCategory): Result<Unit, Error> {
        val duration = Random.nextLong(5_000, 10_000).milliseconds
        return try {
            delay(duration)
            database.withTransactionFacade {
                database.reviewCategoryDao()
                    .insertAll(ReviewCategoryEntity(reviewCategory = reviewCategory))
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toReviewCategoryError())
        }
    }

    override suspend fun findAllReviewCategories(): Flow<Result<List<ReviewCategory>, Error>> {
        return flow {
            val duration = Random.nextLong(5_000, 10_000).milliseconds
            val result: Result<List<ReviewCategory>, Error> = try {
                delay(duration)
                val categories = listOf(
                    ReviewCategory(name = "Staff friendliness"),
                    ReviewCategory(name = "Ambience"),
                    ReviewCategory(name = "Cleanliness"),
                )
                Result.Success(categories)
            } catch (ex: Exception) {
                if (ex is CancellationException) throw ex
                Timber.e(ex)
                Result.Failure(ex.toReviewCategoryError())
            }
            emit(result)
        }
    }
}