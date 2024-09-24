package co.ke.xently.features.recommendations.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.recommendations.data.domain.RecommendationRequest
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.features.recommendations.data.domain.error.DataError
import co.ke.xently.features.recommendations.data.domain.error.Result
import co.ke.xently.features.recommendations.data.source.local.RecommendationDatabase
import co.ke.xently.features.recommendations.data.source.local.RecommendationEntity
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.pagination.data.DataManager
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.RemoteMediator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.fullPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
internal class RecommendationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: RecommendationDatabase,
    private val storeRepository: StoreRepository,
    private val accessControlRepository: AccessControlRepository,
    private val dispatchersProvider: DispatchersProvider,
) : RecommendationRepository, StoreRepository by storeRepository {
    private val recommendationDao = database.recommendationDao()
    override fun findRecommendationById(id: Long): Flow<Result<RecommendationResponse, DataError.Local>> {
        return recommendationDao.findById(id = id).map { entity ->
            if (entity == null) {
                Result.Failure(DataError.Local.ITEM_NOT_FOUND)
            } else {
                Result.Success(entity.recommendation)
            }
        }
    }

    override suspend fun getRecommendationsUrl(): String {
        return accessControlRepository.getAccessControl().recommendationsUrl
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getRecommendations(
        url: String,
        request: RecommendationRequest,
    ): Flow<PagingData<RecommendationResponse>> {
        val pagingConfig = PagingConfig(
            pageSize = 20,
//            initialLoadSize = 20,
//            prefetchDistance = 0,
        )

        val urlString = URLBuilder(url).apply {
            encodedParameters.run {
                set("size", pagingConfig.pageSize.toString())
                parameters.appendMissing("sort", listOf("score,desc", "distance,asc"))
            }
        }.build().fullPath
        val keyManager = LookupKeyManager.URL(url = urlString)

        val dataManager = object : DataManager<RecommendationResponse> {
            override suspend fun insertAll(lookupKey: String, data: List<RecommendationResponse>) {
                recommendationDao.save(
                    data.map { recommendation ->
                        RecommendationEntity(
                            recommendation = recommendation,
                            lookupKey = lookupKey,
                        )
                    },
                )
            }

            override suspend fun deleteByLookupKey(lookupKey: String) {
                recommendationDao.deleteByLookupKey(lookupKey)
            }

            override suspend fun fetchData(url: String?): PagedResponse<RecommendationResponse> {
                return httpClient.post(urlString = url ?: urlString) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body<PagedResponse<RecommendationResponse>>()
            }
        }
        val lookupKey = keyManager.getLookupKey()
        return Pager(
            config = pagingConfig,
            remoteMediator = RemoteMediator(
                database = database,
                keyManager = keyManager,
                dataManager = dataManager,
                dispatchersProvider = dispatchersProvider,
                initialRefreshSkipDuration = Duration.ZERO,
            ),
        ) {
            recommendationDao.getRecommendationsByLookupKey(lookupKey = lookupKey)
        }.flow.map { pagingData ->
            pagingData.map {
                it.recommendation
            }
        }
    }
}