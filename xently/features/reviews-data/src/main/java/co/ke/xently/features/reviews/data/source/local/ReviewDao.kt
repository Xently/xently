package co.ke.xently.features.reviews.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(reviews: List<ReviewEntity>)

    @Query("SELECT * FROM reviews WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getReviewsByLookupKey(lookupKey: String): PagingSource<Int, ReviewEntity>

    @Query("DELETE FROM reviews WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)
}
