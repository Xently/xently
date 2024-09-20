package co.ke.xently.libraries.data.core

import android.os.Parcelable
import androidx.room.TypeConverter
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class Link(
    val href: String = "",
    val templated: Boolean = false,
    val title: String? = null,
    val name: String? = null,
    val rel: String? = null,
) : Parcelable {
    fun hrefWithoutQueryParamTemplates(): String {
        return hrefWithoutContentsFrom('{')
    }

    fun hrefWithoutQueryParams(): String {
        return hrefWithoutContentsFrom('?')
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun hrefWithoutContentsFrom(delimiter: Char): String {
        return href.replaceAfter(delimiter, "")
            .removeSuffix("$delimiter")
    }
}


object RoomTypeConverters {
    private val json = Json(DefaultJson) {
        ignoreUnknownKeys = true
    }

    object LinkConverter {
        @TypeConverter
        fun linkToJson(link: Link): String {
            return json.encodeToString(link)
        }

        @TypeConverter
        fun jsonToLink(link: String): Link {
            return json.decodeFromString(link)
        }
    }
}