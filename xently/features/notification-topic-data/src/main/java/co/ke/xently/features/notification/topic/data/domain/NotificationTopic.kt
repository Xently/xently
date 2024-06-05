package co.ke.xently.features.notification.topic.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NotificationTopic(
    val fcmTopic: String,
    val subscribed: Boolean,
    val topic: Topic,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    @Serializable
    data class Topic(
        val channel: Channel,
        val id: Long,
        val name: String,
        val slug: String,
    ) {
        @Serializable
        data class Channel(
            val id: Long,
            val name: String,
            val slug: String,
        )
    }
}
