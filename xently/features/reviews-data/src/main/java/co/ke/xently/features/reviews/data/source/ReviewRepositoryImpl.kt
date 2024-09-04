package co.ke.xently.features.reviews.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.domain.ReviewRequest
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.ConfigurationError
import co.ke.xently.features.reviews.data.domain.error.Error
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.domain.error.toError
import co.ke.xently.features.reviews.data.source.local.ReviewDatabase
import co.ke.xently.features.reviews.data.source.local.ReviewRequestEntity
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.discardRemaining
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.String
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
    private val reviewRequestDao = database.reviewRequestDao()

    override suspend fun postRating(url: String, message: String?): Result<Unit, Error> {
        val baseURl = url.substringBeforeLast('/')
        val star = url.substringAfterLast('/').toLong()
        val entity = ReviewRequestEntity(
            baseUrl = baseURl,
            star = star.toInt(),
            message = message,
        )
        database.withTransactionFacade {
            reviewRequestDao.save(entity)
        }
        return sync(url = url, message = message, baseUrl = baseURl, star = star)
    }

    override suspend fun syncWithServer() {
        reviewRequestDao.findAll().forEach {
            val baseUrl = it.baseUrl
            val star = it.star.toLong()
            val message = it.message
            val url = URLBuilder(baseUrl)
                .appendPathSegments(star.toString())
                .buildString()
            sync(url = url, message = message, baseUrl = baseUrl, star = star)
        }
    }

    private suspend fun sync(
        url: String,
        message: String?,
        baseUrl: String,
        star: Long,
    ): Result<Unit, Error> {
        return try {
            httpClient.post(url) {
                setBody(ReviewRequest(message))
                contentType(ContentType.Application.Json)
            }.discardRemaining()
            withContext(NonCancellable) {
                database.withTransactionFacade {
                    reviewRequestDao.deleteByBaseUrlAndStar(
                        baseUrl = baseUrl,
                        star = star.toInt(),
                    )
                }
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toError())
        }
    }

    override fun findSummaryReviewForCurrentlyActiveShop(): Flow<Result<Rating, Error>> {
        return shopRepository.findActivatedShop().map { result ->
            when (result) {
                is ShopResult.Failure -> Result.Failure(ConfigurationError.ShopSelectionRequired)
                is ShopResult.Success -> {
                    try {
                        val urlString =
                            result.data.links["reviews-summary"]!!.hrefWithoutQueryParamTemplates()
                        val response = httpClient.get(urlString = urlString)
                            .body<Rating>()
                            .run {
                                copy(
                                    average = average,
                                    totalPerStar = totalPerStar.sortedByDescending { it.star },
                                )
                            }
                        Result.Success(response)
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        Timber.e(ex)
                        Result.Failure(ex.toError())
                    }
                }
            }
        }
    }

    override fun findSummaryReviewForCurrentlyActiveStore(): Flow<Result<Rating, Error>> {
        return storeRepository.findActiveStore().map { result ->
            when (result) {
                is StoreResult.Failure -> Result.Failure(ConfigurationError.StoreSelectionRequired)
                is StoreResult.Success -> {
                    try {
                        val urlString =
                            result.data.links["reviews-summary"]!!.hrefWithoutQueryParamTemplates()
                        val response = httpClient.get(urlString = urlString)
                            .body<Rating>()
                            .run {
                                copy(
                                    average = average,
                                    totalPerStar = totalPerStar.sortedByDescending { it.star },
                                )
                            }
                        Result.Success(response)
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        Timber.e(ex)
                        Result.Failure(ex.toError())
                    }
                }
            }
        }
    }

    override fun findStoreReviewStatistics(
        category: ReviewCategory,
        filters: ReviewStatisticsFilters,
    ): Flow<Result<ReviewCategory.Statistics, Error>> {
        return flow {
            val result: Result<ReviewCategory.Statistics, Error> = try {
                val urlString = category.links["statistics"]!!.hrefWithoutQueryParamTemplates()
                val statistics = httpClient.get(urlString = urlString) {
                    url {
                        encodedParameters.run {
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
                Result.Failure(ex.toError())
            }
            emit(result)
        }
    }

    override suspend fun getReviews(url: String, filters: ReviewFilters): PagedResponse<Review> {
        return httpClient.get(urlString = url) {
            url {
                encodedParameters.run {
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