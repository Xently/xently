package com.kwanzatukule.libraries.data.route.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(route: RouteSummaryEntity)

    @Query("SELECT * FROM route_summary WHERE id = :id")
    fun getRouteSummary(id: Long): Flow<RouteSummaryEntity?>

    @Query("DELETE FROM route_summary WHERE id = :id")
    suspend fun delete(id: Long)
}
