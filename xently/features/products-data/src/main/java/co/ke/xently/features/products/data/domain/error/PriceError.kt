package co.ke.xently.features.products.data.domain.error

enum class PriceError : FieldError {
    INVALID,
    ZERO_OR_LESS,
}