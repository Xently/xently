package co.ke.xently.features.reviews.data.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    @SerialName("averageRating")
    val average: Float,
    @SerialName("perStarRating")
    val totalPerStar: List<Star>,
) {
    val maxStarRating get() = totalPerStar.maxBy { it.star }.star
    val totalReviews get() = totalPerStar.sumOf { it.count }

    @Serializable
    data class Star(@SerialName("starRating") val star: Int, val count: Long)
}