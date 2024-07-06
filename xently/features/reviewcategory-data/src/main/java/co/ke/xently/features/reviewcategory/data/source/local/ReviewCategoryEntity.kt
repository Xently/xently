package co.ke.xently.features.reviewcategory.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory

@Entity(tableName = "review_categories")
data class ReviewCategoryEntity(
    val reviewCategory: ReviewCategory,
    @PrimaryKey
    val name: String = reviewCategory.name,
)
