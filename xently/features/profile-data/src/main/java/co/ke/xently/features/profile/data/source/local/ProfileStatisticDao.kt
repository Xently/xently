package co.ke.xently.features.profile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileStatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(profileStatistic: ProfileStatisticEntity)

    @Query("SELECT * FROM profile_statistics WHERE id = 1")
    fun find(): Flow<ProfileStatisticEntity?>

    @Query("SELECT * FROM profile_statistics WHERE id = 1")
    suspend fun get(): ProfileStatisticEntity?
}