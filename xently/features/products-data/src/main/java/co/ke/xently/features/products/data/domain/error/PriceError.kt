package co.ke.xently.features.products.data.domain.error

enum class PriceError : LocalFieldError {
    INVALID,
    ZERO_OR_LESS,
}