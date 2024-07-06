package co.ke.xently.features.reviewcategory.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewCategory(
    val name: String = "",
    val question: String? = null,
    val myRating: Int = 0,
    val myRatingMessage: String? = null,
    val averageStarRating: Double = 0.0,
    val myLatestRatingDate: Instant? = null,
    val maximumRating: Int = 5,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    @Serializable
    data class SaveRequest(
        val name: String,
    )

    @Serializable
    data class Statistics(
        val totalReviews: Long,
        val generalSentiment: GeneralSentiment,
        val percentageSatisfaction: Int,
        val averageRating: Float,
        @SerialName("_links")
        val links: Map<String, Link> = emptyMap(),
        val years: List<Int> = (2013..2023).toList(),
        val groupedStatistics: List<GroupedStatistic> = emptyList(),
    ) {
        @Suppress("unused")
        enum class GeneralSentiment(val text: String) {
            Positive("+VE"),
            Negative("-VE")
        }

        @Serializable
        data class GroupedStatistic(
            val group: String,
            val starRating: Int,
            val count: Long,
        )
    }

    fun getReviewPostingUrl(starRating: Int): String {
        val link = links["post-review"]!!
        return link.href.replace("{starRating}", starRating.toString())
    }

    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ReviewCategory

        return name == other.name
    }
}
