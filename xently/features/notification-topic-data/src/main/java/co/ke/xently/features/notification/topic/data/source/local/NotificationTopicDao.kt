package co.ke.xently.features.notification.topic.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface NotificationTopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg notificationTopics: NotificationTopicEntity)
}
