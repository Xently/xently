package co.ke.xently.features.recommendations.data.source

import androidx.paging.PagingData
import co.ke.xently.features.recommendations.data.domain.RecommendationRequest
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.features.recommendations.data.domain.error.DataError
import co.ke.xently.features.recommendations.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository : StoreRepository {
    fun findRecommendationById(id: Long): Flow<Result<RecommendationResponse, DataError.Local>>
    suspend fun getRecommendationsUrl(): String
    fun getRecommendations(
        url: String,
        request: RecommendationRequest,
    ): Flow<PagingData<RecommendationResponse>>
}
