package co.ke.xently.features.notification.topic.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationTopicDao {
    @Insert
    suspend fun save(vararg notificationTopics: NotificationTopicEntity)

    @Query("DELETE FROM notification_topics")
    suspend fun deleteAll()

    @Query("SELECT * FROM notification_topics LIMIT 1")
    fun findFirst(): Flow<NotificationTopicEntity?>

    @Query("SELECT * FROM notification_topics LIMIT 1")
    suspend fun first(): NotificationTopicEntity?
}
