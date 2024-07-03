package co.ke.xently.features.recommendations.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg recommendations: RecommendationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(recommendations: List<RecommendationEntity>)

    @Query("DELETE FROM recommendations")
    suspend fun deleteAll()

    @Query("SELECT * FROM recommendations LIMIT 1")
    fun findFirst(): Flow<RecommendationEntity?>

    @Query("SELECT * FROM recommendations LIMIT 1")
    suspend fun first(): RecommendationEntity?

    @Query("SELECT * FROM recommendations WHERE id = :id")
    fun findById(id: Long): Flow<RecommendationEntity?>
}
