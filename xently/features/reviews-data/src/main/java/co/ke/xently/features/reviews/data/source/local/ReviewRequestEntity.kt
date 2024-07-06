package co.ke.xently.features.reviews.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_requests")
data class ReviewRequestEntity(
    @PrimaryKey
    val baseUrl: String,
    val star: Int,
    val message: String? = null,
)