package co.ke.xently.features.reviews.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val starRating: Int,
    val message: String,
    val reviewerName: String? = null,
    val dateCreated: Instant = Clock.System.now(),
    val dateLastModified: Instant? = null,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    fun dateOfReview(): Instant {
        return dateLastModified ?: dateCreated
    }
}
