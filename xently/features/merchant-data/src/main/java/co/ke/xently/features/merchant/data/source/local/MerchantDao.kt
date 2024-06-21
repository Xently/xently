package co.ke.xently.features.merchant.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Insert
    suspend fun save(vararg merchants: MerchantEntity)

    @Query("DELETE FROM merchants")
    suspend fun deleteAll()

    @Query("SELECT * FROM merchants LIMIT 1")
    fun findFirst(): Flow<MerchantEntity?>

    @Query("SELECT * FROM merchants LIMIT 1")
    suspend fun first(): MerchantEntity?
}
