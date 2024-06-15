package co.ke.xently.features.storecategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreCategoryDao {
    @Insert
    suspend fun insertAll(vararg storeCategories: StoreCategoryEntity)

    @Insert
    suspend fun insertAll(storeCategories: List<StoreCategoryEntity>)

    @Query("DELETE FROM store_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM store_categories")
    fun findAll(): Flow<List<StoreCategoryEntity>>
}
