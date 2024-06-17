package co.ke.xently.features.stores.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg stores: StoreEntity)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()

    @Query("SELECT * FROM stores LIMIT 1")
    fun findFirst(): Flow<StoreEntity?>

    @Query("SELECT * FROM stores WHERE isActivated = 1 LIMIT 1")
    fun findActivated(): Flow<StoreEntity?>

    @Query("SELECT * FROM stores LIMIT 1")
    suspend fun first(): StoreEntity?

    @Query("UPDATE stores SET isActivated = 0")
    suspend fun deactivateAll()
}
