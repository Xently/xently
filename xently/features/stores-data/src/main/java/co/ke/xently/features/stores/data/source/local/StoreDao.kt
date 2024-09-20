package co.ke.xently.features.stores.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg stores: StoreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(stores: List<StoreEntity>)

    @Query("SELECT * FROM stores WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getStoresByLookupKey(lookupKey: String): PagingSource<Int, StoreEntity>

    @Query("DELETE FROM stores WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)

    @Query("SELECT * FROM stores WHERE isActivated = 1 ORDER BY dateSaved DESC LIMIT 1")
    suspend fun getActivated(): StoreEntity?

    @Query("SELECT * FROM stores WHERE isActivated = 1 ORDER BY dateSaved DESC LIMIT 1")
    fun findActivated(): Flow<StoreEntity?>

    @Query("UPDATE stores SET isActivated = 0")
    suspend fun deactivateAll()

    @Query("SELECT * FROM stores WHERE id = :id ORDER BY dateSaved DESC LIMIT 1")
    fun findById(id: Long): Flow<StoreEntity?>

    @Query("SELECT isActivated FROM stores WHERE id = :id LIMIT 1")
    suspend fun isActivatedByStoreId(id: Long): Boolean
}
