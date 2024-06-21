package co.ke.xently.features.products.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg products: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT * FROM products LIMIT 1")
    fun findFirst(): Flow<ProductEntity?>

    @Query("SELECT * FROM products LIMIT 1")
    suspend fun first(): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id")
    fun findById(id: Long): Flow<ProductEntity?>
}
