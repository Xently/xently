package co.ke.xently.libraries.location.tracker.domain

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Stable
@Parcelize
data class Location(
    @SerialName("latitude")
    val latitude: Double = Double.NaN,
    @SerialName("longitude")
    val longitude: Double = Double.NaN,
    @Transient
    val name: String? = null,
    val averageCoordinates: Double = ((latitude + longitude) / 2),
) : Parcelable {
    fun isUsable() = (!latitude.isNaN()
            || !longitude.isNaN())

    override fun toString(): String {
        return if (!name.isNullOrBlank()) {
            name
        } else {
            coordinatesString()
        }
    }

    fun coordinatesString() = "x=$longitude,y=$latitude"

    object Saver : androidx.compose.runtime.saveable.Saver<MutableState<Location?>, String> {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
        }

        override fun restore(value: String): MutableState<Location?> {
            return mutableStateOf(json.decodeFromString(value))
        }

        override fun SaverScope.save(value: MutableState<Location?>): String {
            return json.encodeToString(value.value)
        }
    }
}