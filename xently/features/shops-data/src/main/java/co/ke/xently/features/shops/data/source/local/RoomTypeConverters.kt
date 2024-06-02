package co.ke.xently.features.shops.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.shops.data.domain.Shop
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object ShopConverter {
        @TypeConverter
        fun shopToJson(shop: Shop): String {
            return json.encodeToString(shop)
        }

        @TypeConverter
        fun jsonToShop(shop: String): Shop {
            return json.decodeFromString(shop)
        }
    }
}