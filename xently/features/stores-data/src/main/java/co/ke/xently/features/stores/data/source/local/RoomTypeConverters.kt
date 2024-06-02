package co.ke.xently.features.stores.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.stores.data.domain.Store
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object StoreConverter {
        @TypeConverter
        fun storeToJson(store: Store): String {
            return json.encodeToString(store)
        }

        @TypeConverter
        fun jsonToStore(store: String): Store {
            return json.decodeFromString(store)
        }
    }
}