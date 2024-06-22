package co.ke.xently.libraries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServerResponseCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(serverResponseCache: ServerResponseCache)

    @Query("SELECT * FROM server_response_cache WHERE id = :id")
    suspend fun findById(id: String): ServerResponseCache?
}