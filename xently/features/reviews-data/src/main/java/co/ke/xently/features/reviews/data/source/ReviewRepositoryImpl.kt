package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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

    override suspend fun findSummaryReviewForCurrentlyActiveShop(): Flow<Result<Rating, DataError>> {
        return flow {
            val duration = Random.nextLong(1_000, 5_000).milliseconds
            val result: Result<Rating, DataError> = try {
                delay(duration)
                val rating = Rating(
                    average = 3.5f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, Random.nextLong(10_000, 1_000_000))
                    }.sortedByDescending { it.star },
                )
                Result.Success(rating)
            } catch (ex: Exception) {
                if (ex is CancellationException) throw ex
                Timber.e(ex)
                Result.Failure(DataError.Network.entries.random())
            }
            emit(result)
        }
    }

    override suspend fun findSummaryReviewForCurrentlyActiveStore(): Flow<Result<Rating, DataError>> {
        return flow {
            val duration = Random.nextLong(1_000, 5_000).milliseconds
            val result: Result<Rating, DataError> = try {
                delay(duration)
                val rating = Rating(
                    average = 4.5f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, Random.nextLong(10_000, 100_000))
                    }.sortedByDescending { it.star },
                )
                Result.Success(rating)
            } catch (ex: Exception) {
                if (ex is CancellationException) throw ex
                Timber.e(ex)
                Result.Failure(DataError.Network.entries.random())
            }
            emit(result)
        }
    }

    override suspend fun findStoreReviewStatistics(
        category: ReviewCategory,
        filters: ReviewStatisticsFilters,
    ): Flow<Result<ReviewCategory.Statistics, DataError>> {
        return flow {
            val duration = Random.nextLong(1_000, 5_000).milliseconds
            val result: Result<ReviewCategory.Statistics, DataError> = try {
                delay(duration)
                val statistics = ReviewCategory.Statistics(
                    totalReviews = 100,
                    generalSentiment = ReviewCategory.Statistics.GeneralSentiment.entries.random(),
                    averageRating = 3.7f,
                    percentageSatisfaction = Random.nextInt(0, 100),
                    groupedStatistics = List(10) {
                        ReviewCategory.Statistics.GroupedStatistic(
                            group = "Group $it",
                            starRating = Random.nextInt(1, 5),
                            count = 100,
                        )
                    },
                )
                Result.Success(statistics)
            } catch (ex: Exception) {
                if (ex is CancellationException) throw ex
                Timber.e(ex)
                Result.Failure(DataError.Network.entries.random())
            }
            emit(result)
        }
    }

    data class Placeholder(
        val body: String,
        val id: Int,
        val title: String,
        val userId: Int,
    )

    override suspend fun getReviews(url: String?, filters: ReviewFilters): PagedResponse<Review> {
        val body = httpClient.get("https://jsonplaceholder.typicode.com/posts")

        val reviews = List(20) {
            Review(
                starRating = Random.nextInt(1, 5),
                message = """Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""",
                links = mapOf(
                    "self" to Link(href = "https://jsonplaceholder.typicode.com/posts/${it + 1}")
                ),
            )
        }
        return PagedResponse(embedded = mapOf("views" to reviews))
    }
}