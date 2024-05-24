package com.kwanzatukule.features.customer.complaints.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerComplaintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(customers: List<CustomerComplaintEntity>)

    @Query("SELECT * FROM customers ORDER BY id ASC")
    fun getCustomerComplaints(): Flow<List<CustomerComplaintEntity>>

    @Query("DELETE FROM customers")
    suspend fun clear()
}
