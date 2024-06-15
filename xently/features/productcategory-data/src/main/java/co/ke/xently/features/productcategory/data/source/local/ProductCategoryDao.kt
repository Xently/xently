package co.ke.xently.features.productcategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCategoryDao {
    @Insert
    suspend fun insertAll(vararg productCategories: ProductCategoryEntity)

    @Insert
    suspend fun insertAll(productCategories: List<ProductCategoryEntity>)

    @Query("DELETE FROM product_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM product_categories")
    fun findAll(): Flow<List<ProductCategoryEntity>>
}
