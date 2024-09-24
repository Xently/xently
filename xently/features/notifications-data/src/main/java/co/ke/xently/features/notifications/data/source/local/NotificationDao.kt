package co.ke.xently.features.notifications.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(notifications: List<NotificationEntity>)

    @Query("SELECT * FROM notifications WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getNotificationsByLookupKey(lookupKey: String): PagingSource<Int, NotificationEntity>

    @Query("DELETE FROM notifications WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)
}
