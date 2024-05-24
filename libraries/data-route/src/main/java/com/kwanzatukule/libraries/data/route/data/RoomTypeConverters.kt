package com.kwanzatukule.libraries.data.route.data

import androidx.room.TypeConverter
import com.kwanzatukule.libraries.data.route.domain.Route
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object RouteConverter {
        @TypeConverter
        fun routeToJson(item: Route): String {
            return json.encodeToString(item)
        }

        @TypeConverter
        fun jsonToRoute(item: String): Route {
            return json.decodeFromString(item)
        }
    }
}