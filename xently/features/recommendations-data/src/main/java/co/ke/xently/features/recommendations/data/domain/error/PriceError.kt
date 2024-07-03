package co.ke.xently.features.recommendations.data.domain.error

enum class PriceError : LocalFieldError {
    INVALID,
    ZERO_OR_LESS,
}