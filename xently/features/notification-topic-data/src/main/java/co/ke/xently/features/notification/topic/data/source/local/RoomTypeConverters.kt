package co.ke.xently.features.notification.topic.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.notification.topic.data.domain.NotificationTopic
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object NotificationTopicConverter {
        @TypeConverter
        fun notificationTopicToJson(notificationTopic: NotificationTopic): String {
            return json.encodeToString(notificationTopic)
        }

        @TypeConverter
        fun jsonToNotificationTopic(notificationTopic: String): NotificationTopic {
            return json.decodeFromString(notificationTopic)
        }
    }
}