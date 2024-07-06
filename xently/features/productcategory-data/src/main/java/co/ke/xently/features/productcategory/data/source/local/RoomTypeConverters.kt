package co.ke.xently.features.productcategory.data.source.local

import androidx.room.TypeConverter
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object ProductCategoryConverter {
        @TypeConverter
        fun productCategoryToJson(productCategory: ProductCategory): String {
            return json.encodeToString(productCategory)
        }

        @TypeConverter
        fun jsonToProductCategory(productCategory: String): ProductCategory {
            return json.decodeFromString(productCategory)
        }
    }
}