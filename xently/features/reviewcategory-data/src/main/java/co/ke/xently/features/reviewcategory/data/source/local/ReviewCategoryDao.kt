package co.ke.xently.features.reviewcategory.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg reviewCategories: ReviewCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviewCategories: List<ReviewCategoryEntity>)

    @Query("DELETE FROM review_categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM review_categories WHERE name = :name LIMIT 1")
    fun findByName(name: String): Flow<ReviewCategoryEntity?>

    @Query("SELECT * FROM review_categories LIMIT 1")
    suspend fun first(): ReviewCategoryEntity?
}
