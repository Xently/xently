package co.ke.xently.features.shops.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Insert
    suspend fun insertAll(vararg shops: ShopEntity)

    @Query("DELETE FROM shops")
    suspend fun deleteAll()

    @Query("SELECT * FROM shops LIMIT 1")
    fun findFirst(): Flow<ShopEntity?>

    @Query("SELECT * FROM shops LIMIT 1")
    suspend fun first(): ShopEntity?
}
