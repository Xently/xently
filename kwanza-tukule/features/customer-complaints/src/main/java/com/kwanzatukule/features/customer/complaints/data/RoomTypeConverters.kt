package com.kwanzatukule.features.customer.complaints.data

import androidx.room.TypeConverter
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object CustomerComplaintConverter {
        @TypeConverter
        fun customerToJson(item: CustomerComplaint): String {
            return json.encodeToString(item)
        }

        @TypeConverter
        fun jsonToCustomerComplaint(item: String): CustomerComplaint {
            return json.decodeFromString(item)
        }
    }
}