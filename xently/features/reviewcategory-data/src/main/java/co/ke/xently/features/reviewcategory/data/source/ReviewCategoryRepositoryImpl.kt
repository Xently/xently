package co.ke.xently.features.reviewcategory.data.source

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.ConfigurationError
import co.ke.xently.features.reviewcategory.data.domain.error.DataError
import co.ke.xently.features.reviewcategory.data.domain.error.Error
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import co.ke.xently.features.reviewcategory.data.domain.error.toError
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryDatabase
import co.ke.xently.features.reviewcategory.data.source.local.ReviewCategoryEntity
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.String
import kotlin.Unit
import kotlin.also
import kotlin.coroutines.cancellation.CancellationException
import kotlin.run
import co.ke.xently.features.stores.data.domain.error.Result as StoreResult

@Singleton
internal class ReviewCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ReviewCategoryDatabase,
    private val storeRepository: StoreRepository,
) : ReviewCategoryRepository {
    private val reviewCategoryDao = database.reviewCategoryDao()
    override suspend fun save(reviewCategory: ReviewCategory): Result<Unit, Error> {
        val body = ReviewCategory.SaveRequest(
            name = reviewCategory.name,
        )
        val store = when (val result = storeRepository.getActiveStore()) {
            is co.ke.xently.features.stores.data.domain.error.Result.Failure -> {
                return Result.Failure(ConfigurationError.valueOf(result.error.name))
            }

            is co.ke.xently.features.stores.data.domain.error.Result.Success -> result.data
        }

        return try {
            val urlString = store.links["review-categories"]!!.hrefWithoutQueryParams()
            val response = httpClient.post(urlString) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<ReviewCategory>()
            database.withTransactionFacade {
                reviewCategoryDao
                    .save(ReviewCategoryEntity(reviewCategory = response))
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toError())
        }
    }

    override fun findCategoryByName(name: String): Flow<Result<ReviewCategory, DataError.Network.ResourceNotFound>> {
        return reviewCategoryDao.findByName(name = name).map { entity ->
            if (entity == null) {
                Result.Failure(DataError.Network.ResourceNotFound)
            } else {
                Result.Success(entity.reviewCategory)
            }
        }
    }

    override fun findAllReviewCategories(): Flow<Result<List<ReviewCategory>, Error>> {
        return storeRepository.findActiveStore().map { result ->
            when (result) {
                is StoreResult.Failure -> Result.Failure(ConfigurationError.StoreSelectionRequired)
                is StoreResult.Success -> {
                    try {
                        val urlString =
                            result.data.links["review-categories"]!!.hrefWithoutQueryParamTemplates()
                        val response = httpClient.get(urlString = urlString)
                            .body<PagedResponse<ReviewCategory>>().run {
                                (embedded.values.firstOrNull() ?: emptyList()).also { categories ->
                                    coroutineScope {
                                        launch {
                                            database.withTransactionFacade {
                                                reviewCategoryDao.deleteAll()
                                                reviewCategoryDao.save(
                                                    categories.map {
                                                        ReviewCategoryEntity(reviewCategory = it)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
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

    override suspend fun findReviewCategories(url: String): PagedResponse<ReviewCategory> {
        return httpClient.get(urlString = url)
            .body<PagedResponse<ReviewCategory>>()
    }
}