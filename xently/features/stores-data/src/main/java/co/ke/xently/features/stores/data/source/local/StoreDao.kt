package co.ke.xently.features.stores.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert
    suspend fun insertAll(vararg stores: StoreEntity)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()

    @Query("SELECT * FROM stores LIMIT 1")
    fun findFirst(): Flow<StoreEntity?>

    @Query("SELECT * FROM stores LIMIT 1")
    suspend fun first(): StoreEntity?
}
