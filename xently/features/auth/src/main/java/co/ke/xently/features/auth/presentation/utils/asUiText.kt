package co.ke.xently.features.auth.presentation.utils

import co.ke.xently.features.auth.R
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.auth.data.domain.error.Error
import co.ke.xently.features.auth.data.domain.error.GoogleAuthenticationError
import co.ke.xently.features.auth.data.domain.error.PasswordError

fun Error.asUiText(): UiText {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.the_request_timed_out
        )

        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.youve_hit_your_rate_limit
        )

        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.no_internet
        )

        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.file_too_large
        )

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.server_error
        )

        DataError.Network.SERIALIZATION -> UiText.StringResource(
            R.string.error_serialization
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.unknown_error
        )

        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.error_disk_full
        )

        GoogleAuthenticationError.INVALID_GOOGLE_ID_RESPONSE -> UiText.StringResource(R.string.error_google_sign_in_invalid_google_id_response)
        GoogleAuthenticationError.UNRECOGNISED_CREDENTIAL_TYPE -> UiText.StringResource(R.string.error_google_sign_in_unrecongised_credential_type)
        GoogleAuthenticationError.CANCELLED -> UiText.StringResource(R.string.error_google_sign_in_cancelled)
        GoogleAuthenticationError.INTERRUPTED -> UiText.StringResource(R.string.error_google_sign_in_interrupted)
        GoogleAuthenticationError.UNSUPPORTED_PROVIDER -> UiText.StringResource(R.string.error_google_sign_in_unsupported_provider)
        GoogleAuthenticationError.NO_CREDENTIALS_FOUND -> UiText.StringResource(R.string.error_google_sign_in_no_credentials_found)
        GoogleAuthenticationError.UNEXPECTED_ERROR -> UiText.StringResource(R.string.error_google_sign_in_unexpected_error)
        PasswordError.TOO_SHORT -> UiText.StringResource(R.string.error_password_too_short)
        PasswordError.NO_UPPERCASE -> UiText.StringResource(R.string.error_password_no_uppercase)
        PasswordError.NO_DIGIT -> UiText.StringResource(R.string.error_password_no_digit)
    }
}
