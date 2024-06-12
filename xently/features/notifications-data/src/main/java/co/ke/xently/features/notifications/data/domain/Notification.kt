package co.ke.xently.features.notifications.data.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Notification(
    val id: Long,
    val timeSent: Instant,
    @SerialName("data")
    val message: Message,
) {
    @Serializable
    data class Message(
        val title: String = "",
        val message: String = "",
        val topic: String = "",
        val channel: Channel = Channel(),
    ) {
        @Serializable
        data class Channel(
            val id: String = "",
            val name: String = "",
            val group: Group? = null,
        ) {
            @Serializable
            data class Group(
                val id: String,
                val name: String,
            )
        }
    }
}