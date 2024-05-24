package com.kwanzatukule.libraries.data.customer.data

import androidx.room.TypeConverter
import com.kwanzatukule.libraries.data.customer.domain.Customer
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object CustomerConverter {
        @TypeConverter
        fun customerToJson(item: Customer): String {
            return json.encodeToString(item)
        }

        @TypeConverter
        fun jsonToCustomer(item: String): Customer {
            return json.decodeFromString(item)
        }
    }
}