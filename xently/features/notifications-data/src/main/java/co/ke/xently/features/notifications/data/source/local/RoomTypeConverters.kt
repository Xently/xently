package co.ke.xently.features.notifications.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.notifications.data.domain.Notification
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object NotificationConverter {
        @TypeConverter
        fun notificationToJson(notification: Notification): String {
            return json.encodeToString(notification)
        }

        @TypeConverter
        fun jsonToNotification(notification: String): Notification {
            return json.decodeFromString(notification)
        }
    }
}