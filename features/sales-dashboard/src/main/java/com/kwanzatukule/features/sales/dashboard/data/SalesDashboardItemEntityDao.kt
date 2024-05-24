package com.kwanzatukule.features.sales.dashboard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDashboardItemEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entities: List<SalesDashboardItemEntity>)

    @Query("SELECT * FROM sales_dashboard_items ORDER BY id ASC")
    fun getSalesDashboardItems(): Flow<List<SalesDashboardItemEntity>>

    @Query("DELETE FROM sales_dashboard_items")
    suspend fun clearEntries()
}
