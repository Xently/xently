package com.kwanzatukule.features.sales.dashboard.data

import androidx.room.TypeConverter
import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object SalesDashboardItemConverter {
        @TypeConverter
        fun itemToJson(item: SalesDashboardItem): String {
            return json.encodeToString(item)
        }

        @TypeConverter
        fun jsonToSalesDashboardItem(item: String): SalesDashboardItem {
            return json.decodeFromString(item)
        }
    }
}