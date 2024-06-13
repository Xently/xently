package co.ke.xently.features.reviews.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

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

    companion object {
        val DEFAULT = Review(
            starRating = Random.nextInt(1, 6),
            message = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
            reviewerName = "John Doe",
            links = mapOf(
                "self" to Link("https://jsonplaceholder.typicode.com/posts/1")
            ),
        )
    }
}
