package com.kwanzatukule.features.authentication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertAll(vararg users: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user LIMIT 1")
    fun findFirst(): Flow<User?>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun first(): User?
}
