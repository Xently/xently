package co.ke.xently.features.profile.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object ProfileStatisticConverter {
        @TypeConverter
        fun statisticToJson(statistic: ProfileStatistic): String {
            return json.encodeToString(statistic)
        }

        @TypeConverter
        fun jsonToProfileStatistic(statistic: String): ProfileStatistic {
            return json.decodeFromString(statistic)
        }
    }
}