package co.ke.xently.libraries.location.tracker.presentation.utils

import co.ke.xently.libraries.location.tracker.R
import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.LocationRequestError
import co.ke.xently.libraries.location.tracker.domain.error.PermissionError
import co.ke.xently.libraries.location.tracker.domain.error.Result

fun Error.asUiText(): UiText {
    return when (this) {
        LocationRequestError.UNKNOWN -> UiText.StringResource(R.string.unknown_error)
        PermissionError.GPS_DISABLED -> UiText.StringResource(R.string.error_gps_disabled)
        PermissionError.PERMISSION_DENIED -> UiText.StringResource(R.string.error_permission_denied)
        LocationRequestError.NO_KNOWN_LOCATION -> UiText.StringResource(R.string.error_no_known_location)
    }
}

fun Result.Failure<*, Error>.asErrorUiText(): UiText {
    return error.asUiText()
}