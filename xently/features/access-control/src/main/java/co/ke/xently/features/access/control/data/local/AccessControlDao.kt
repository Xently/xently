package co.ke.xently.features.access.control.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessControlDao {
    @Insert
    suspend fun save(accessControl: AccessControlEntity)

    @Query("SELECT * FROM access_controls LIMIT 1")
    fun findFirst(): Flow<AccessControlEntity?>
}
