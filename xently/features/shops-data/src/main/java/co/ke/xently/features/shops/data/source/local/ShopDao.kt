package co.ke.xently.features.shops.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg shops: ShopEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(shops: List<ShopEntity>)

    @Query("SELECT * FROM shops WHERE id = :id ORDER BY dateSaved DESC LIMIT 1")
    fun findById(id: Long): Flow<ShopEntity?>

    @Query("SELECT * FROM shops WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getShopsByLookupKey(lookupKey: String): PagingSource<Int, ShopEntity>

    @Query("DELETE FROM shops WHERE lookupKey = :lookupKey AND isActivated = 0")
    fun deleteByLookupKeyExceptActivated(lookupKey: String)

    @Query("SELECT * FROM shops WHERE isActivated = 1 ORDER BY dateSaved DESC LIMIT 1")
    suspend fun getActivated(): ShopEntity?

    @Query("SELECT * FROM shops WHERE isActivated = 1 ORDER BY dateSaved DESC LIMIT 1")
    fun findActivated(): Flow<ShopEntity?>

    @Query("SELECT * FROM shops ORDER BY isActivated DESC, dateSaved DESC, id DESC LIMIT 10")
    fun findTop10ShopsOrderByIsActivated(): Flow<List<ShopEntity>>

    @Query("UPDATE shops SET isActivated = 0")
    suspend fun deactivateAll()
}
