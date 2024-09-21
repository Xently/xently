package co.ke.xently.features.productcategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg productCategories: ProductCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(productCategories: List<ProductCategoryEntity>)

    @Query("DELETE FROM product_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM product_categories")
    fun findAll(): Flow<List<ProductCategoryEntity>>

    @Query("SELECT * FROM product_categories")
    suspend fun getAll(): List<ProductCategoryEntity>
}
