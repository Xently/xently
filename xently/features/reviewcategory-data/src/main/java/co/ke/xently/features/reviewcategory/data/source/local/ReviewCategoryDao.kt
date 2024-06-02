package co.ke.xently.features.reviewcategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewCategoryDao {
    @Insert
    suspend fun insertAll(vararg reviewCategories: ReviewCategoryEntity)

    @Query("DELETE FROM review_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM review_categories LIMIT 1")
    fun findFirst(): Flow<ReviewCategoryEntity?>

    @Query("SELECT * FROM review_categories LIMIT 1")
    suspend fun first(): ReviewCategoryEntity?
}
