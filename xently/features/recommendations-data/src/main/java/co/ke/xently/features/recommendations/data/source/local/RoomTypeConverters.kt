package co.ke.xently.features.recommendations.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object RecommendationConverter {
        @TypeConverter
        fun recommendationToJson(recommendation: RecommendationResponse): String {
            return json.encodeToString(recommendation)
        }

        @TypeConverter
        fun jsonToRecommendation(recommendation: String): RecommendationResponse {
            return json.decodeFromString(recommendation)
        }
    }
}