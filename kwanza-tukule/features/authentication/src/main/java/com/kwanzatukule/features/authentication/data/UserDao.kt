package com.kwanzatukule.features.authentication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg users: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user LIMIT 1")
    fun findFirst(): Flow<UserEntity?>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun first(): UserEntity?
}
