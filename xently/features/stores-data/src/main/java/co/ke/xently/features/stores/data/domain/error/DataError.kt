package co.ke.xently.features.stores.data.domain.error

import co.ke.xently.features.stores.data.R
import co.ke.xently.libraries.data.core.AuthorisationError
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.data.core.UiText

sealed interface DataError : Error {
    sealed interface Network : DataError {
        data object RequestTimeout : Network, RetryableError
        data object NoInternet : Network, RetryableError
        data object TooEarly : Network, RetryableError
        data object Locked : Network, RetryableError
        data object Conflict : Network, RetryableError
        data object Permission : Network, RetryableError
        data object ServerError : Network, RetryableError

        data object InvalidCredentials : Network
        data object ResourceNotFound : Network, RetryableError
        data object Unauthorized : Network, AuthorisationError
        data object ResourceMoved : Network, RetryableError
        data object BadRequest : Network, RetryableError
        data object MethodNotAllowed : Network, RetryableError
        data object NotAcceptable : Network, RetryableError
        data object LengthRequired : Network, RetryableError
        data object RequestURITooLong : Network, RetryableError
        data object UnsupportedMediaType : Network, RetryableError
        data object RequestedRangeNotSatisfiable : Network, RetryableError
        data object ExpectationFailed : Network, RetryableError
        data object UnprocessableEntity : Network, RetryableError
        data object PreconditionFailed : Network, RetryableError
        data object UpgradeRequired : Network, RetryableError
        data object RequestHeaderFieldTooLarge : Network, RetryableError
        data object FailedDependency : Network, RetryableError
        data object TooManyRequests : Network, RetryableError
        data object PayloadTooLarge : Network, RetryableError
        data object Serialization : Network, RetryableError
    }

    enum class Local : DataError {
        DISK_FULL
    }
    override suspend fun toUiText(): UiText {
        return when (this) {
            Network.RequestTimeout -> UiText.StringResource(R.string.the_request_timed_out)
            Network.TooManyRequests -> UiText.StringResource(R.string.youve_hit_your_rate_limit)
            Network.NoInternet -> UiText.StringResource(R.string.no_internet)
            Network.PayloadTooLarge -> UiText.StringResource(R.string.file_too_large)
            Network.ServerError -> UiText.StringResource(R.string.server_error)
            Network.Serialization -> UiText.StringResource(R.string.error_serialization)
            Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
            Network.ResourceNotFound -> UiText.StringResource(R.string.error_message_missing_resource)
            Network.Unauthorized -> UiText.StringResource(R.string.error_message_authentication_required)
            Network.Permission -> UiText.StringResource(R.string.error_message_authorisation_required)
            Network.ResourceMoved -> UiText.StringResource(R.string.error_message_missing_resource_moved)
            Network.BadRequest -> UiText.StringResource(R.string.error_message_bad_request)
            Network.MethodNotAllowed -> UiText.StringResource(R.string.error_message_default)
            Network.NotAcceptable -> UiText.StringResource(R.string.error_message_not_acceptable)
            Network.Conflict -> UiText.StringResource(R.string.error_message_conflict)
            Network.LengthRequired -> UiText.StringResource(R.string.error_message_length_required)
            Network.RequestURITooLong -> UiText.StringResource(R.string.error_message_default)
            Network.UnsupportedMediaType -> UiText.StringResource(R.string.error_message_unsupported_media_type)
            Network.RequestedRangeNotSatisfiable -> UiText.StringResource(R.string.error_message_range_not_satisfiable)
            Network.ExpectationFailed -> UiText.StringResource(R.string.error_message_expectation_failed)
            Network.UnprocessableEntity -> UiText.StringResource(R.string.error_message_unprocessable_entity)
            Network.PreconditionFailed -> UiText.StringResource(R.string.error_message_precondition_failed)
            Network.Locked -> UiText.StringResource(R.string.error_message_locked)
            Network.TooEarly -> UiText.StringResource(R.string.error_message_too_early)
            Network.UpgradeRequired -> UiText.StringResource(R.string.error_message_upgrade_required)
            Network.RequestHeaderFieldTooLarge -> UiText.StringResource(R.string.error_message_request_header_too_large)
            Network.FailedDependency -> UiText.StringResource(R.string.error_message_failed_dependency)
            Network.InvalidCredentials -> UiText.StringResource(R.string.error_message_invalid_auth_credentials)
        }
    }
}