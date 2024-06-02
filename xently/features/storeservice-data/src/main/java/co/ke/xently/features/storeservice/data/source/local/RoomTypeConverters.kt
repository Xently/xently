package co.ke.xently.features.storeservice.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.storeservice.data.domain.StoreService
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object StoreServiceConverter {
        @TypeConverter
        fun storeServiceToJson(storeService: StoreService): String {
            return json.encodeToString(storeService)
        }

        @TypeConverter
        fun jsonToStoreService(storeService: String): StoreService {
            return json.decodeFromString(storeService)
        }
    }
}