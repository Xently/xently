package co.ke.xently.features.merchant.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.merchant.data.domain.Merchant
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object MerchantConverter {
        @TypeConverter
        fun merchantToJson(merchant: Merchant): String {
            return json.encodeToString(merchant)
        }

        @TypeConverter
        fun jsonToMerchant(merchant: String): Merchant {
            return json.decodeFromString(merchant)
        }
    }
}