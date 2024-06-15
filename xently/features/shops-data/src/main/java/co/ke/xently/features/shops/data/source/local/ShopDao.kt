package co.ke.xently.features.shops.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg shops: ShopEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shops: List<ShopEntity>)

    @Query("DELETE FROM shops")
    suspend fun deleteAll()

    @Query("SELECT * FROM shops LIMIT 1")
    fun findFirst(): Flow<ShopEntity?>

    @Query("SELECT * FROM shops LIMIT 1")
    suspend fun first(): ShopEntity?
    @Query("SELECT * FROM shops ORDER BY isActivated, id DESC LIMIT 10")
    fun findTop10ShopsOrderByIsActivated(): Flow<List<ShopEntity>>
}
