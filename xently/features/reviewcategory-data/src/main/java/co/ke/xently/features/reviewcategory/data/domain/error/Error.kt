package co.ke.xently.features.reviewcategory.data.domain.error


import co.ke.xently.libraries.data.network.ApiErrorResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import timber.log.Timber


sealed interface Error

suspend fun Throwable.toError(): Error {
    return when (this) {
        is ResponseException -> toError()
        is JsonConvertException -> {
            Timber.e(this)
            DataError.Network.Serialization
        }

        else -> {
            Timber.e(this)
            UnknownError
        }
    }
}

private suspend fun ResponseException.toError(): Error {
    val error = response.body<ApiErrorResponse>().run {
        when (code) {
            in setOf("authentication_failed", "token_exchange_failed") -> {
                DataError.Network.InvalidCredentials
            }

            "empty_fcm_device_ids" -> FCMDeviceRegistrationRequired
            else -> null
        }
    }
    if (error != null) return error

    return when (response.status) {
        HttpStatusCode.NotFound -> DataError.Network.ResourceNotFound
        HttpStatusCode.Unauthorized -> DataError.Network.Unauthorized
        HttpStatusCode.ProxyAuthenticationRequired -> DataError.Network.Unauthorized
        HttpStatusCode.Forbidden -> DataError.Network.Retryable.Permission
        HttpStatusCode.Gone -> DataError.Network.ResourceMoved
        HttpStatusCode.BadRequest -> DataError.Network.BadRequest
        HttpStatusCode.MethodNotAllowed -> DataError.Network.MethodNotAllowed
        HttpStatusCode.NotAcceptable -> DataError.Network.NotAcceptable
        HttpStatusCode.RequestTimeout -> DataError.Network.Retryable.RequestTimeout
        HttpStatusCode.Conflict -> DataError.Network.Retryable.Conflict
        HttpStatusCode.LengthRequired -> DataError.Network.LengthRequired
        HttpStatusCode.PreconditionFailed -> DataError.Network.PreconditionFailed
        HttpStatusCode.PayloadTooLarge -> DataError.Network.PayloadTooLarge
        HttpStatusCode.RequestURITooLong -> DataError.Network.RequestURITooLong
        HttpStatusCode.UnsupportedMediaType -> DataError.Network.UnsupportedMediaType
        HttpStatusCode.RequestedRangeNotSatisfiable -> DataError.Network.RequestedRangeNotSatisfiable
        HttpStatusCode.ExpectationFailed -> DataError.Network.ExpectationFailed
        HttpStatusCode.UnprocessableEntity -> DataError.Network.UnprocessableEntity
        HttpStatusCode.Locked -> DataError.Network.Retryable.Locked
        HttpStatusCode.FailedDependency -> DataError.Network.FailedDependency
        HttpStatusCode.TooEarly -> DataError.Network.Retryable.TooEarly
        HttpStatusCode.UpgradeRequired -> DataError.Network.UpgradeRequired
        HttpStatusCode.TooManyRequests -> DataError.Network.TooManyRequests
        HttpStatusCode.RequestHeaderFieldTooLarge -> DataError.Network.RequestHeaderFieldTooLarge
        else -> DataError.Network.Retryable.ServerError
    }
}