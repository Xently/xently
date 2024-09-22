package co.ke.xently.features.stores.data.domain.error

import co.ke.xently.features.stores.data.R
import co.ke.xently.libraries.data.core.UiText

enum class LocationError : LocalFieldError {
    INVALID_FORMAT,
    INVALID_LATITUDE,
    INVALID_LONGITUDE,
    MISSING;

    override suspend fun toUiText(): UiText {
        return when (this) {
            INVALID_FORMAT -> UiText.StringResource(R.string.error_location_invalid_format)
            INVALID_LATITUDE -> UiText.StringResource(R.string.error_location_invalid_latitude)
            INVALID_LONGITUDE -> UiText.StringResource(R.string.error_location_invalid_longitude)
            MISSING -> UiText.StringResource(R.string.error_location_missing)
        }
    }
}