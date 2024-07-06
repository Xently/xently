package co.ke.xently.features.customers.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.customers.data.domain.Customer
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object CustomerConverter {
        @TypeConverter
        fun customerToJson(customer: Customer): String {
            return json.encodeToString(customer)
        }

        @TypeConverter
        fun jsonToCustomer(customer: String): Customer {
            return json.decodeFromString(customer)
        }
    }
}