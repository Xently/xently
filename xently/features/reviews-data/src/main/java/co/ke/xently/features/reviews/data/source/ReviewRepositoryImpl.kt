package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.ConfigurationError
import co.ke.xently.features.reviews.data.domain.error.Error
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.domain.error.toReviewError
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.String
import kotlin.TODO
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.run
import kotlin.to
import co.ke.xently.features.shops.data.domain.error.Result as ShopResult
import co.ke.xently.features.stores.data.domain.error.Result as StoreResult

@Singleton
internal class ReviewRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ReviewDatabase,
    private val shopRepository: ShopRepository,
    private val storeRepository: StoreRepository,
) : ReviewRepository {
    override suspend fun save(review: Review): Result<Unit, Error> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Review, Error>> {
        TODO("Not yet implemented")
    }

    override suspend fun findSummaryReviewForCurrentlyActiveShop(): Flow<Result<Rating, Error>> {
        return shopRepository.findActivatedShop().map { result ->
            when (result) {
                is ShopResult.Failure -> Result.Failure(ConfigurationError.ShopSelectionRequired)
                is ShopResult.Success -> {
                    val urlString =
                        result.data.links["reviews-summary"]!!.hrefWithoutQueryParamTemplates()
                    val response =
                        httpClient.get(urlString = urlString)
                            .body<Rating>()
                            .run {
                                copy(
                                    average = average,
                                    totalPerStar = totalPerStar.sortedByDescending { it.star },
                                )
                            }
                    Result.Success(response)
                }
            }
        }
    }

    override suspend fun findSummaryReviewForCurrentlyActiveStore(): Flow<Result<Rating, Error>> {
        return storeRepository.findActiveStore().map { result ->
            when (result) {
                is StoreResult.Failure -> Result.Failure(ConfigurationError.StoreSelectionRequired)
                is StoreResult.Success -> {
                    val urlString =
                        result.data.links["reviews-summary"]!!.hrefWithoutQueryParamTemplates()
                    val response =
                        httpClient.get(urlString = urlString)
                            .body<Rating>()
                            .run {
                                copy(
                                    average = average,
                                    totalPerStar = totalPerStar.sortedByDescending { it.star },
                                )
                            }
                    Result.Success(response)
                }
            }
        }
    }

    override suspend fun findStoreReviewStatistics(
        category: ReviewCategory,
        filters: ReviewStatisticsFilters,
    ): Flow<Result<ReviewCategory.Statistics, Error>> {
        return flow {
            val result: Result<ReviewCategory.Statistics, Error> = try {
                val urlString = category.links["statistics"]!!.hrefWithoutQueryParamTemplates()
                val statistics = httpClient.get(urlString = urlString) {
                    url {
                        parameters.run {
                            if (filters.year != null) {
                                set("year", filters.year.toString())
                            }
                            if (filters.month != null) {
                                set("month", filters.month.toString())
                            }
                        }
                    }
                }.body<ReviewCategory.Statistics>()
                Result.Success(statistics)
            } catch (ex: Exception) {
                if (ex is CancellationException) throw ex
                Result.Failure(ex.toReviewError())
            }
            emit(result)
        }
    }

    override suspend fun getReviews(url: String, filters: ReviewFilters): PagedResponse<Review> {
        return httpClient.get(urlString = url) {
            url {
                parameters.run {
                    set("hasComments", filters.hasComments.toString())
                    if (filters.starRating != null) {
                        set("starRating", filters.starRating.toString())
                    }
                }
            }
        }.body<PagedResponse<Review>>().run {
            copy(embedded = mapOf("views" to (embedded.values.firstOrNull() ?: emptyList())))
        }
    }
}