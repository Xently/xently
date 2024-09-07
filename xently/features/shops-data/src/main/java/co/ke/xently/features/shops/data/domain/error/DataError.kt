package co.ke.xently.features.shops.data.domain.error

sealed interface DataError : Error {
    sealed interface Network : DataError {
        sealed interface Retryable : Network {
            data object RequestTimeout : Retryable
            data object NoInternet : Retryable
            data object TooEarly : Retryable
            data object Locked : Retryable
            data object Conflict : Retryable
            data object Permission : Retryable
            data object ServerError : Retryable
            data object Unknown : Network
        }

        data object InvalidCredentials : Network
        data object ResourceNotFound : Retryable
        data object Unauthorized : Network
        data object ResourceMoved : Retryable
        data object BadRequest : Retryable
        data object MethodNotAllowed : Retryable
        data object NotAcceptable : Retryable
        data object LengthRequired : Retryable
        data object RequestURITooLong : Retryable
        data object UnsupportedMediaType : Retryable
        data object RequestedRangeNotSatisfiable : Retryable
        data object ExpectationFailed : Retryable
        data object UnprocessableEntity : Retryable
        data object PreconditionFailed : Retryable
        data object UpgradeRequired : Retryable
        data object RequestHeaderFieldTooLarge : Retryable
        data object FailedDependency : Retryable
        data object TooManyRequests : Retryable
        data object PayloadTooLarge : Retryable
        data object Serialization : Retryable
    }

    enum class Local : DataError {
        DISK_FULL
    }
}