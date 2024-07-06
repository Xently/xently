package co.ke.xently.features.customers.data.domain.error

import co.ke.xently.libraries.data.network.ApiErrorResponse
import io.ktor.client.call.DoubleReceiveException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import timber.log.Timber


sealed interface Error

suspend fun Throwable.toError(): Error {
    return when (this) {
        is ResponseException -> toError()
        is HttpRequestTimeoutException -> DataError.Network.Retryable.RequestTimeout
        is ShopSelectionRequiredException -> ConfigurationError.ShopSelectionRequired
        is StoreSelectionRequiredException -> ConfigurationError.StoreSelectionRequired
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

private fun ApiErrorResponse.toError(): Error? {
    return when (code) {
        in setOf("authentication_failed", "token_exchange_failed") -> {
            DataError.Network.InvalidCredentials
        }

        "empty_fcm_device_ids" -> FCMDeviceRegistrationRequired
        else -> null
    }
}

private suspend fun ResponseException.toError(): Error {
    val error = try {
        response.body<ApiErrorResponse>().toError()
    } catch (ex: NoTransformationFoundException) {
        null
    } catch (ex: DoubleReceiveException) {
        null
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