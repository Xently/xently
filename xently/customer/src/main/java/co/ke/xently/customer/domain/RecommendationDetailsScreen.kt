package co.ke.xently.customer.domain

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDetailsScreen(
    val productsUrl: String,
    val recommendationId: Long = -1,
)