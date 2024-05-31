package co.ke.xently.libraries.location.tracker.domain

import kotlinx.serialization.Serializable

@Serializable
data class DirectionNavigation(
    val api: String? = null,
    val travelMode: String? = "driving",
    val origin: Location? = null,
    val destination: Location? = null,
    val waypoints: List<Location> = emptyList(),
) {
    fun getGoogleMapsDirectionUrl(): String {
        return buildString {
            append("https://www.google.com/maps/dir/?api=${api ?: "1"}")
            if (travelMode != null) {
                append("&travelmode=")
                append(travelMode)
            }
            if (origin != null) {
                append("&origin=")
                append(origin.latitude)
                append(",")
                append(origin.longitude)
            }
            if (destination != null) {
                append("&destination=")
                append(destination.latitude)
                append(",")
                append(destination.longitude)
            }
            with(waypoints) {
                if (isNotEmpty()) {
                    append("&waypoints=")
                    forEachIndexed { index, location ->
                        append(location.latitude)
                        append(",")
                        append(location.longitude)
                        if (index < lastIndex) {
                            append("|")
                        }
                    }
                }
            }
        }
    }
}