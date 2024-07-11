package co.ke.xently.features.auth.presentation.utils

import co.ke.xently.features.auth.R
import co.ke.xently.features.auth.data.domain.error.*

private fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Network.Retryable.RequestTimeout -> UiText.StringResource(R.string.the_request_timed_out)
        DataError.Network.TooManyRequests -> UiText.StringResource(R.string.youve_hit_your_rate_limit)
        DataError.Network.Retryable.NoInternet -> UiText.StringResource(R.string.no_internet)
        DataError.Network.PayloadTooLarge -> UiText.StringResource(R.string.file_too_large)
        DataError.Network.Retryable.ServerError -> UiText.StringResource(R.string.server_error)
        DataError.Network.Serialization -> UiText.StringResource(R.string.error_serialization)
        DataError.Network.Retryable.Unknown -> UiText.StringResource(R.string.error_message_default)
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        DataError.Network.ResourceNotFound -> UiText.StringResource(R.string.error_message_missing_resource)
        DataError.Network.Unauthorized -> UiText.StringResource(R.string.error_message_authentication_required)
        DataError.Network.Retryable.Permission -> UiText.StringResource(R.string.error_message_authorisation_required)
        DataError.Network.ResourceMoved -> UiText.StringResource(R.string.error_message_missing_resource_moved)
        DataError.Network.BadRequest -> UiText.StringResource(R.string.error_message_bad_request)
        DataError.Network.MethodNotAllowed -> UiText.StringResource(R.string.error_message_default)
        DataError.Network.NotAcceptable -> UiText.StringResource(R.string.error_message_not_acceptable)
        DataError.Network.Retryable.Conflict -> UiText.StringResource(R.string.error_message_conflict)
        DataError.Network.LengthRequired -> UiText.StringResource(R.string.error_message_length_required)
        DataError.Network.RequestURITooLong -> UiText.StringResource(R.string.error_message_default)
        DataError.Network.UnsupportedMediaType -> UiText.StringResource(R.string.error_message_unsupported_media_type)
        DataError.Network.RequestedRangeNotSatisfiable -> UiText.StringResource(R.string.error_message_range_not_satisfiable)
        DataError.Network.ExpectationFailed -> UiText.StringResource(R.string.error_message_expectation_failed)
        DataError.Network.UnprocessableEntity -> UiText.StringResource(R.string.error_message_unprocessable_entity)
        DataError.Network.PreconditionFailed -> UiText.StringResource(R.string.error_message_precondition_failed)
        DataError.Network.Retryable.Locked -> UiText.StringResource(R.string.error_message_locked)
        DataError.Network.Retryable.TooEarly -> UiText.StringResource(R.string.error_message_too_early)
        DataError.Network.UpgradeRequired -> UiText.StringResource(R.string.error_message_upgrade_required)
        DataError.Network.RequestHeaderFieldTooLarge -> UiText.StringResource(R.string.error_message_request_header_too_large)
        DataError.Network.FailedDependency -> UiText.StringResource(R.string.error_message_failed_dependency)
        DataError.Network.InvalidCredentials -> UiText.StringResource(R.string.error_message_invalid_auth_credentials)
    }
}

private fun FieldError.asUiText(): UiText {
    return when (this) {
        PasswordError.TOO_SHORT -> UiText.StringResource(R.string.error_password_too_short)
        PasswordError.NO_UPPERCASE -> UiText.StringResource(R.string.error_password_no_uppercase)
        PasswordError.NO_DIGIT -> UiText.StringResource(R.string.error_password_no_digit)
    }
}

fun Error.asUiText(): UiText {
    return when (this) {
        is DataError -> asUiText()
        is FieldError -> asUiText()
        is UnknownError -> UiText.StringResource(R.string.error_message_default)

        GoogleAuthenticationError.INVALID_GOOGLE_ID_RESPONSE -> UiText.StringResource(R.string.error_google_sign_in_invalid_google_id_response)
        GoogleAuthenticationError.UNRECOGNISED_CREDENTIAL_TYPE -> UiText.StringResource(R.string.error_google_sign_in_unrecongised_credential_type)
        GoogleAuthenticationError.CANCELLED -> UiText.StringResource(R.string.error_google_sign_in_cancelled)
        GoogleAuthenticationError.INTERRUPTED -> UiText.StringResource(R.string.error_google_sign_in_interrupted)
        GoogleAuthenticationError.UNSUPPORTED_PROVIDER -> UiText.StringResource(R.string.error_google_sign_in_unsupported_provider)
        GoogleAuthenticationError.NO_CREDENTIALS_FOUND -> UiText.StringResource(R.string.error_google_sign_in_no_credentials_found)
        GoogleAuthenticationError.UNEXPECTED_ERROR -> UiText.StringResource(R.string.error_google_sign_in_unexpected_error)
    }
}
