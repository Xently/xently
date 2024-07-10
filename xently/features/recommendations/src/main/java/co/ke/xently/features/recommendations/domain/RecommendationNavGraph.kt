package co.ke.xently.features.recommendations.domain

import kotlinx.serialization.Serializable

@Serializable
data object RecommendationNavGraph {
    @Serializable
    internal data object RecommendationRequestScreen

    @Serializable
    internal data object RecommendationResponseScreen

    @Serializable
    internal data class RecommendationDetailsScreen(
        val productsUrl: String,
        val recommendationId: Long = -1,
    )
}
