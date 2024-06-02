package co.ke.xently.features.reviews.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert
    suspend fun insertAll(vararg reviews: ReviewEntity)

    @Query("DELETE FROM reviews")
    suspend fun deleteAll()

    @Query("SELECT * FROM reviews LIMIT 1")
    fun findFirst(): Flow<ReviewEntity?>

    @Query("SELECT * FROM reviews LIMIT 1")
    suspend fun first(): ReviewEntity?
}
