package co.ke.xently.features.reviews.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(request: ReviewRequestEntity)

    @Query("SELECT * FROM review_requests")
    suspend fun findAll(): List<ReviewRequestEntity>

    @Query("DELETE FROM review_requests WHERE baseUrl = :baseUrl AND star = :star")
    suspend fun deleteByBaseUrlAndStar(baseUrl: String, star: Int)
}