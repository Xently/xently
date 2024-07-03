package co.ke.xently.features.recommendations.data.source

import co.ke.xently.features.recommendations.data.domain.RecommendationRequest
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.features.recommendations.data.domain.error.Error
import co.ke.xently.features.recommendations.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun findById(id: Long): Flow<Result<RecommendationResponse, Error>>
    suspend fun getRecommendations(
        url: String?,
        request: RecommendationRequest,
    ): PagedResponse<RecommendationResponse>
}
