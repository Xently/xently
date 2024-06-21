package co.ke.xently.features.notifications.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg notifications: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

    @Query("SELECT * FROM notifications LIMIT 1")
    fun findFirst(): Flow<NotificationEntity?>

    @Query("SELECT * FROM notifications LIMIT 1")
    suspend fun first(): NotificationEntity?
}
