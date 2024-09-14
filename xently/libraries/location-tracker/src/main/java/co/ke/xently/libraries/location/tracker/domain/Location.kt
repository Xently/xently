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
    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

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

fun android.location.Location.toXentlyLocation(): Location {
    return Location(
        latitude = latitude,
        longitude = longitude,
    )
}

fun Location.toAndroidLocation(): android.location.Location {
    return android.location.Location(null).apply {
        this.latitude = this@toAndroidLocation.latitude
        this.longitude = this@toAndroidLocation.longitude
    }
}