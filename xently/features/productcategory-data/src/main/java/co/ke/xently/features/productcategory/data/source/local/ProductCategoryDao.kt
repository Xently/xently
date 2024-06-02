package co.ke.xently.features.productcategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCategoryDao {
    @Insert
    suspend fun insertAll(vararg productCategories: ProductCategoryEntity)

    @Query("DELETE FROM product_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM product_categories LIMIT 1")
    fun findFirst(): Flow<ProductCategoryEntity?>

    @Query("SELECT * FROM product_categories LIMIT 1")
    suspend fun first(): ProductCategoryEntity?
}
