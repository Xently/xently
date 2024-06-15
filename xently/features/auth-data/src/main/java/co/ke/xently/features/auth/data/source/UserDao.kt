package co.ke.xently.features.auth.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertAll(vararg users: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user LIMIT 1")
    fun findFirst(): Flow<UserEntity?>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun first(): UserEntity?
}
