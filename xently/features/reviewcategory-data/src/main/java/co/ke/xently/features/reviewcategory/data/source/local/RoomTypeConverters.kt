package co.ke.xently.features.reviewcategory.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object ReviewCategoryConverter {
        @TypeConverter
        fun reviewCategoryToJson(reviewCategory: ReviewCategory): String {
            return json.encodeToString(reviewCategory)
        }

        @TypeConverter
        fun jsonToReviewCategory(reviewCategory: String): ReviewCategory {
            return json.decodeFromString(reviewCategory)
        }
    }
}