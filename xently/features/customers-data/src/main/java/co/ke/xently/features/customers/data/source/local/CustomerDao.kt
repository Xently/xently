package co.ke.xently.features.customers.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg customers: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(customers: List<CustomerEntity>)

    @Query("DELETE FROM customers")
    suspend fun deleteAll()

    @Query("SELECT * FROM customers LIMIT 1")
    fun findFirst(): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers LIMIT 1")
    suspend fun first(): CustomerEntity?
}
