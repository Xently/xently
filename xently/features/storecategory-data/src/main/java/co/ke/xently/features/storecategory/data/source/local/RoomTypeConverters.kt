package co.ke.xently.features.storecategory.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object StoreCategoryConverter {
        @TypeConverter
        fun storeCategoryToJson(storeCategory: StoreCategory): String {
            return json.encodeToString(storeCategory)
        }

        @TypeConverter
        fun jsonToStoreCategory(storeCategory: String): StoreCategory {
            return json.decodeFromString(storeCategory)
        }
    }
}