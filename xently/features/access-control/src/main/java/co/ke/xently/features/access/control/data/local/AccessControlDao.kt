package co.ke.xently.features.access.control.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessControlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(accessControl: AccessControlEntity)

    @Query("SELECT * FROM access_controls WHERE id = 1")
    fun findFirst(): Flow<AccessControlEntity?>

    @Query("SELECT * FROM access_controls WHERE id = 1")
    suspend fun first(): AccessControlEntity?
}
