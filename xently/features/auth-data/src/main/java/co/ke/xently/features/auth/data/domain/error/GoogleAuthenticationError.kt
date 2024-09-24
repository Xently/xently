package co.ke.xently.features.auth.data.domain.error

import co.ke.xently.features.auth.data.R
import co.ke.xently.libraries.data.core.UiText

enum class GoogleAuthenticationError : Error {
    INVALID_GOOGLE_ID_RESPONSE,
    UNRECOGNISED_CREDENTIAL_TYPE,
    CANCELLED,
    INTERRUPTED,
    UNSUPPORTED_PROVIDER,
    NO_CREDENTIALS_FOUND,
    UNEXPECTED_ERROR;

    override suspend fun toUiText(): UiText {
        return when (this) {
            INVALID_GOOGLE_ID_RESPONSE -> UiText.StringResource(R.string.error_google_sign_in_invalid_google_id_response)
            UNRECOGNISED_CREDENTIAL_TYPE -> UiText.StringResource(R.string.error_google_sign_in_unrecognised_credential_type)
            CANCELLED -> UiText.StringResource(R.string.error_google_sign_in_cancelled)
            INTERRUPTED -> UiText.StringResource(R.string.error_google_sign_in_interrupted)
            UNSUPPORTED_PROVIDER -> UiText.StringResource(R.string.error_google_sign_in_unsupported_provider)
            NO_CREDENTIALS_FOUND -> UiText.StringResource(R.string.error_google_sign_in_no_credentials_found)
            UNEXPECTED_ERROR -> UiText.StringResource(R.string.error_google_sign_in_unexpected_error)
        }
    }
}