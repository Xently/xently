package co.ke.xently.features.reviews.data.domain.error

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
        data object ResourceNotFound : Network
        data object Unauthorized : Network
        data object ResourceMoved : Network
        data object BadRequest : Network
        data object MethodNotAllowed : Network
        data object NotAcceptable : Network
        data object LengthRequired : Network
        data object RequestURITooLong : Network
        data object UnsupportedMediaType : Network
        data object RequestedRangeNotSatisfiable : Network
        data object ExpectationFailed : Network
        data object UnprocessableEntity : Network
        data object PreconditionFailed : Network
        data object UpgradeRequired : Network
        data object RequestHeaderFieldTooLarge : Network
        data object FailedDependency : Network
        data object TooManyRequests : Network
        data object PayloadTooLarge : Network
        data object Serialization : Network
    }

    enum class Local : DataError {
        DISK_FULL
    }
}