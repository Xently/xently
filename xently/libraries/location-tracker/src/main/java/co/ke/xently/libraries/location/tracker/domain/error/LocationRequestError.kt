package co.ke.xently.libraries.location.tracker.domain.error

import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.libraries.location.tracker.R

enum class LocationRequestError : Error {
    UNKNOWN,
    NO_KNOWN_LOCATION;

    override suspend fun toUiText(): UiText {
        return when (this) {
            UNKNOWN -> UiText.StringResource(R.string.unknown_error)
            NO_KNOWN_LOCATION -> UiText.StringResource(R.string.error_no_known_location)
        }
    }
}