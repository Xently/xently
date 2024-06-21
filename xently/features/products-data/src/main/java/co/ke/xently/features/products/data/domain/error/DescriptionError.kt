package co.ke.xently.features.products.data.domain.error

sealed interface DescriptionError : LocalFieldError {
    data class TooLong(val acceptableLength: Int) : DescriptionError
}