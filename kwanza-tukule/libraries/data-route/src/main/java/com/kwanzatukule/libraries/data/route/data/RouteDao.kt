package com.kwanzatukule.libraries.data.route.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(routes: List<RouteEntity>)

    @Query("SELECT * FROM routes ORDER BY id ASC")
    fun getRoutes(): Flow<List<RouteEntity>>

    @Query("DELETE FROM routes")
    suspend fun clear()
}
