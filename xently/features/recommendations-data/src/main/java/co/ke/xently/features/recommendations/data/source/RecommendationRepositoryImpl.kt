package co.ke.xently.features.recommendations.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.recommendations.data.domain.RecommendationRequest
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.features.recommendations.data.domain.error.DataError
import co.ke.xently.features.recommendations.data.domain.error.Error
import co.ke.xently.features.recommendations.data.domain.error.Result
import co.ke.xently.features.recommendations.data.source.local.RecommendationDatabase
import co.ke.xently.features.recommendations.data.source.local.RecommendationEntity
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RecommendationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: RecommendationDatabase,
    private val accessControlRepository: AccessControlRepository,
) : RecommendationRepository {
    private val recommendationDao = database.recommendationDao()
    override fun findById(id: Long): Flow<Result<RecommendationResponse, Error>> {
        return recommendationDao.findById(id = id).map { entity ->
            if (entity == null) {
                Result.Failure(DataError.Network.ResourceNotFound)
            } else {
                Result.Success(entity.recommendation)
            }
        }
    }

    override suspend fun getRecommendations(
        url: String?,
        request: RecommendationRequest,
    ): PagedResponse<RecommendationResponse> {
        val urlString = url ?: accessControlRepository.getAccessControl().recommendationsUrl
        return httpClient.post(urlString = urlString) {
            url {
                parameters.appendMissing("sort", listOf("score,desc", "distance,asc"))
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<PagedResponse<RecommendationResponse>>().run {
            (embedded.values.firstOrNull() ?: emptyList()).let { recommendations ->
                coroutineScope {
                    launch {
                        recommendationDao.save(recommendations.map {
                            RecommendationEntity(
                                recommendation = it
                            )
                        })
                    }
                }
                copy(embedded = mapOf("views" to recommendations))
            }
        }
    }
}