package com.kwanzatukule.libraries.data.customer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(customers: List<CustomerEntity>)

    @Query("SELECT * FROM customers ORDER BY id ASC")
    fun getCustomers(): Flow<List<CustomerEntity>>

    @Query("DELETE FROM customers")
    suspend fun clear()
}
