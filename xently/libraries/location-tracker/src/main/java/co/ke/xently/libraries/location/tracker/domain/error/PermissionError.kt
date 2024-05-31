package co.ke.xently.libraries.location.tracker.domain.error

enum class PermissionError : Error {
    GPS_DISABLED,
    PERMISSION_DENIED,
}