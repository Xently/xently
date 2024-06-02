package co.ke.xently.features.reviews.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.reviews.data.domain.ReviewCategory

@Entity(tableName = "review_categories")
data class ReviewCategoryEntity(
    val category: ReviewCategory,
    @PrimaryKey
    val id: Long = category.id,
)