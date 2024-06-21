package co.ke.xently.features.storeservice.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreServiceDao {
    @Insert
    suspend fun save(vararg storeCategories: StoreServiceEntity)

    @Query("DELETE FROM store_services")
    suspend fun deleteAll()

    @Query("SELECT * FROM store_services LIMIT 1")
    fun findFirst(): Flow<StoreServiceEntity?>

    @Query("SELECT * FROM store_services LIMIT 1")
    suspend fun first(): StoreServiceEntity?
}
