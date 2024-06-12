package co.ke.xently.features.stores.data.domain.error

enum class LocationError : FieldError {
    INVALID_FORMAT,
    INVALID_LATITUDE,
    INVALID_LONGITUDE,
    MISSING,
}