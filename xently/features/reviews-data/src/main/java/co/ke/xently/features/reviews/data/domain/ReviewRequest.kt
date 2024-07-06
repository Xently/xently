package co.ke.xently.features.reviews.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(val message: String? = null)
