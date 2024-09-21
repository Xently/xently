package co.ke.xently.features.recommendations.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(recommendations: List<RecommendationEntity>)

    @Query("SELECT * FROM recommendations WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getRecommendationsByLookupKey(lookupKey: String): PagingSource<Int, RecommendationEntity>

    @Query("DELETE FROM recommendations WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)

    @Query("SELECT * FROM recommendations WHERE id = :id")
    fun findById(id: Long): Flow<RecommendationEntity?>
}
