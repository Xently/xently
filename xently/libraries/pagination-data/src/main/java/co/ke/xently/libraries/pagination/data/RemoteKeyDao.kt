package co.ke.xently.libraries.pagination.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE lookupKey = :lookupKey")
    suspend fun remoteKeyByLookupKey(lookupKey: String): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE lookupKey = :lookupKey")
    suspend fun deleteByLookupKey(lookupKey: String)
}