package co.ke.xently.features.stores.data.domain.error

enum class LocationError : LocalFieldError {
    INVALID_FORMAT,
    INVALID_LATITUDE,
    INVALID_LONGITUDE,
    MISSING,
}