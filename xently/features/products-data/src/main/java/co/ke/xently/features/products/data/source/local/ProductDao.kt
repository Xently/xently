package co.ke.xently.features.products.data.source.local

import androidx.paging.PagingSource
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

    @Query("SELECT * FROM products WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getProductsByLookupKey(lookupKey: String): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM products WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)

    @Query("SELECT * FROM products WHERE id = :id ORDER BY dateSaved DESC LIMIT 1")
    fun findById(id: Long): Flow<ProductEntity?>
}
