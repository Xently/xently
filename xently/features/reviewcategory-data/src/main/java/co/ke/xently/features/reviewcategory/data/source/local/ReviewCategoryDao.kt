package co.ke.xently.features.reviewcategory.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg reviewCategories: ReviewCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(reviewCategories: List<ReviewCategoryEntity>)

    @Query("SELECT * FROM review_categories WHERE lookupKey = :lookupKey ORDER BY dateSaved")
    fun getReviewCategoriesByLookupKey(lookupKey: String): PagingSource<Int, ReviewCategoryEntity>

    @Query("DELETE FROM review_categories WHERE lookupKey = :lookupKey")
    fun deleteByLookupKey(lookupKey: String)

    @Query("DELETE FROM review_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM review_categories WHERE name = :name LIMIT 1")
    fun findByName(name: String): Flow<ReviewCategoryEntity?>
}
