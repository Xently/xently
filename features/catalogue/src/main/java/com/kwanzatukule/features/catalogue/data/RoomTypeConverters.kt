package com.kwanzatukule.features.catalogue.data

import androidx.room.TypeConverter
import com.kwanzatukule.features.catalogue.domain.Product
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object ProductConverter {
        @TypeConverter
        fun productToJson(product: Product): String {
            return json.encodeToString(product)
        }

        @TypeConverter
        fun jsonToProduct(product: String): Product {
            return json.decodeFromString(product)
        }
    }
}