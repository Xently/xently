package co.ke.xently.libraries.location.tracker.domain.error

import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.libraries.location.tracker.R

enum class PermissionError : Error {
    GPS_DISABLED,
    PERMISSION_DENIED;

    override suspend fun toUiText(): UiText {
        return when (this) {
            GPS_DISABLED -> UiText.StringResource(R.string.error_gps_disabled)
            PERMISSION_DENIED -> UiText.StringResource(R.string.error_permission_denied)
        }
    }
}