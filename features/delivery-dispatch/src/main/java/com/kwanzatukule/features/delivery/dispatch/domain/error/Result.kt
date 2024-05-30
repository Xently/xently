package com.kwanzatukule.features.delivery.dispatch.domain.error


sealed interface Result<out D, out E : Error> {
    data class Success<out D, out E : Error>(val data: D) : Result<D, E>
    data class Failure<out D, out E : Error>(val error: E) : Result<D, E>
}