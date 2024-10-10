package co.ke.xently.features.storecategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoreCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(storeCategories: List<StoreCategoryEntity>)

    @Query("DELETE FROM store_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM store_categories")
    suspend fun getAll(): List<StoreCategoryEntity>
}
