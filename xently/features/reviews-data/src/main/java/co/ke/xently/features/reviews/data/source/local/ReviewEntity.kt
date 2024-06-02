package co.ke.xently.features.reviews.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.reviews.data.domain.Review

@Entity(tableName = "reviews")
data class ReviewEntity(
    val review: Review,
    @PrimaryKey
    val id: Long = review.id,
)
