package co.ke.xently.features.auth.data.domain.error

enum class PasswordError : FieldError {
    TOO_SHORT,
    NO_UPPERCASE,
    NO_DIGIT
}