package co.ke.xently.features.reviewcategory.presentation.utils

import co.ke.xently.features.reviewcategory.R
import co.ke.xently.features.reviewcategory.data.domain.error.ConfigurationError
import co.ke.xently.features.reviewcategory.data.domain.error.DataError
import co.ke.xently.features.reviewcategory.data.domain.error.Error
import co.ke.xently.features.reviewcategory.data.domain.error.FCMDeviceRegistrationRequired
import co.ke.xently.features.reviewcategory.data.domain.error.UnknownError

fun Error.asUiText(): UiText {
    return when (this) {
        DataError.Network.RequestTimeout -> UiText.StringResource(R.string.the_request_timed_out)
        DataError.Network.TooManyRequests -> UiText.StringResource(R.string.youve_hit_your_rate_limit)
        DataError.Network.NoInternet -> UiText.StringResource(R.string.no_internet)
        DataError.Network.PayloadTooLarge -> UiText.StringResource(R.string.file_too_large)
        DataError.Network.ServerError -> UiText.StringResource(R.string.server_error)
        DataError.Network.Serialization -> UiText.StringResource(R.string.error_serialization)
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        DataError.Network.ResourceNotFound -> UiText.StringResource(R.string.error_message_missing_resource)
        DataError.Network.Unauthorized -> UiText.StringResource(R.string.error_message_authentication_required)
        DataError.Network.Permission -> UiText.StringResource(R.string.error_message_authorisation_required)
        DataError.Network.ResourceMoved -> UiText.StringResource(R.string.error_message_missing_resource_moved)
        DataError.Network.BadRequest -> UiText.StringResource(R.string.error_message_bad_request)
        DataError.Network.MethodNotAllowed -> UiText.StringResource(R.string.error_message_default)
        DataError.Network.NotAcceptable -> UiText.StringResource(R.string.error_message_not_acceptable)
        DataError.Network.Conflict -> UiText.StringResource(R.string.error_message_conflict)
        DataError.Network.LengthRequired -> UiText.StringResource(R.string.error_message_length_required)
        DataError.Network.RequestURITooLong -> UiText.StringResource(R.string.error_message_default)
        DataError.Network.UnsupportedMediaType -> UiText.StringResource(R.string.error_message_unsupported_media_type)
        DataError.Network.RequestedRangeNotSatisfiable -> UiText.StringResource(R.string.error_message_range_not_satisfiable)
        DataError.Network.ExpectationFailed -> UiText.StringResource(R.string.error_message_expectation_failed)
        DataError.Network.UnprocessableEntity -> UiText.StringResource(R.string.error_message_unprocessable_entity)
        DataError.Network.PreconditionFailed -> UiText.StringResource(R.string.error_message_precondition_failed)
        DataError.Network.Locked -> UiText.StringResource(R.string.error_message_locked)
        DataError.Network.TooEarly -> UiText.StringResource(R.string.error_message_too_early)
        DataError.Network.UpgradeRequired -> UiText.StringResource(R.string.error_message_upgrade_required)
        DataError.Network.RequestHeaderFieldTooLarge -> UiText.StringResource(R.string.error_message_request_header_too_large)
        DataError.Network.FailedDependency -> UiText.StringResource(R.string.error_message_failed_dependency)
        UnknownError -> UiText.StringResource(R.string.error_message_default)
        ConfigurationError.StoreSelectionRequired -> UiText.StringResource(R.string.error_store_not_selected)
        ConfigurationError.ShopSelectionRequired -> UiText.StringResource(R.string.error_shop_not_selected)
        FCMDeviceRegistrationRequired -> UiText.StringResource(R.string.error_message_fcm_device_registration_required)
        DataError.Network.InvalidCredentials -> UiText.StringResource(R.string.error_message_invalid_auth_credentials)
    }
}