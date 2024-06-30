package co.ke.xently.features.profile.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileStatistic(
    val points: Statistic = Statistic(
        stat = "0",
        name = "Points",
    ),
    val bookmarks: Statistic = Statistic(
        stat = "0",
        name = "Bookmarks",
    ),
    val placesVisited: Statistic = Statistic(
        stat = "0",
        name = "Places visited",
    ),
) {
    @Serializable
    data class Statistic(
        val stat: String,
        val name: String,
        @SerialName("_links")
        val links: Map<String, Link> = emptyMap(),
    )

    companion object {
        val DEFAULT = ProfileStatistic()
    }
}
