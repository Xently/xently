package co.ke.xently.features.recommendations.domain

import kotlinx.serialization.Serializable

@Serializable
data object RecommendationNavGraph {
    @Serializable
    internal data object RecommendationRequest

    @Serializable
    internal data object RecommendationResponse
}