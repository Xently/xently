package co.ke.xently.features.notification.topic.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.notification.topic.data.domain.NotificationTopic

@Entity(tableName = "notification_topics")
data class NotificationTopicEntity(
    val notificationTopic: NotificationTopic,
    @PrimaryKey
    val id: Long = notificationTopic.topic.id,
)
