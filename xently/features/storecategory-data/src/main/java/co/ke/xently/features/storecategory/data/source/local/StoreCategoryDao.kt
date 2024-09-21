package co.ke.xently.features.storecategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg storeCategories: StoreCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(storeCategories: List<StoreCategoryEntity>)

    @Query("DELETE FROM store_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM store_categories")
    fun findAll(): Flow<List<StoreCategoryEntity>>

    @Query("SELECT * FROM store_categories")
    suspend fun getAll(): List<StoreCategoryEntity>
}
