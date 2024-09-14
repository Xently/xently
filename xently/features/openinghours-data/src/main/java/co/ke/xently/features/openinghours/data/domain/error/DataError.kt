package co.ke.xently.features.openinghours.data.domain.error

import co.ke.xently.libraries.data.core.AuthorisationError
import co.ke.xently.libraries.data.core.RetryableError

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
}