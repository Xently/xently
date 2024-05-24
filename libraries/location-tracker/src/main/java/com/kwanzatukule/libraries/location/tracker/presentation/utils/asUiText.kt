package com.kwanzatukule.libraries.location.tracker.presentation.utils

import com.kwanzatukule.libraries.location.tracker.R
import com.kwanzatukule.libraries.location.tracker.domain.error.Error
import com.kwanzatukule.libraries.location.tracker.domain.error.LocationRequestError
import com.kwanzatukule.libraries.location.tracker.domain.error.PermissionError
import com.kwanzatukule.libraries.location.tracker.domain.error.Result

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