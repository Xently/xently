package co.ke.xently.features.reviews.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewCategory
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object ReviewConverter {
        @TypeConverter
        fun reviewToJson(review: Review): String {
            return json.encodeToString(review)
        }

        @TypeConverter
        fun jsonToReview(review: String): Review {
            return json.decodeFromString(review)
        }
    }

    object ReviewCategoryConverter {
        @TypeConverter
        fun categoryToJson(category: ReviewCategory): String {
            return json.encodeToString(category)
        }

        @TypeConverter
        fun jsonToReviewCategory(category: String): ReviewCategory {
            return json.decodeFromString(category)
        }
    }
}