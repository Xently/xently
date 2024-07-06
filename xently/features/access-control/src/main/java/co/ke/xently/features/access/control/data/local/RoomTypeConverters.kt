package co.ke.xently.features.access.control.data.local

import androidx.room.TypeConverter
import co.ke.xently.features.access.control.domain.AccessControl
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object AccessControlConverter {
        @TypeConverter
        fun storeToJson(store: AccessControl): String {
            return json.encodeToString(store)
        }

        @TypeConverter
        fun jsonToAccessControl(store: String): AccessControl {
            return json.decodeFromString(store)
        }
    }
}