package co.ke.xently.features.auth.data.domain.error

enum class PasswordError : Error {
    TOO_SHORT,
    NO_UPPERCASE,
    NO_DIGIT
}