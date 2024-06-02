package co.ke.xently.features.products.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insertAll(vararg products: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT * FROM products LIMIT 1")
    fun findFirst(): Flow<ProductEntity?>

    @Query("SELECT * FROM products LIMIT 1")
    suspend fun first(): ProductEntity?
}
