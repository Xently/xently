package co.ke.xently.libraries.data.core

import androidx.room.TypeConverter
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object LinkConverter {
        @TypeConverter
        fun linkToJson(link: Link): String {
            return json.encodeToString(link)
        }

        @TypeConverter
        fun jsonToLink(link: String): Link {
            return json.decodeFromString(link)
        }
    }
}