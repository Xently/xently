package co.ke.xently.features.customers.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(customers: List<CustomerEntity>)

    @Query("SELECT * FROM customers WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getCustomersByLookupKey(lookupKey: String): PagingSource<Int, CustomerEntity>

    @Query("DELETE FROM customers WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)
}
