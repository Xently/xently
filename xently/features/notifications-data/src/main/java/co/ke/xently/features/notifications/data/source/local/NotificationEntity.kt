package co.ke.xently.features.notifications.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.notifications.data.domain.Notification

@Entity(tableName = "notifications")
data class NotificationEntity(
    val notification: Notification,
    @PrimaryKey
    val id: Long = notification.topic.id,
)
