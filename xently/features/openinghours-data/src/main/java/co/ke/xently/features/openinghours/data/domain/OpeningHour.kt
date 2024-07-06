package co.ke.xently.features.openinghours.data.domain

import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.core.Time
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpeningHour(
    @SerialName("closeTime")
    val closeTime: Time,
    @SerialName("dayOfWeek")
    val dayOfWeek: DayOfWeek,
    @SerialName("open")
    val open: Boolean,
    @SerialName("openTime")
    val openTime: Time,
    @SerialName("_links")
    val links: Links = Links(),
) {
    @Serializable
    data class Links(
        @SerialName("self")
        val self: Link = Link(),
    )
}
