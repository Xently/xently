package co.ke.xently.features.auth.data.domain.error

sealed interface PasswordError : LocalFieldError {
    class TooShort(val minimumLength: Int) : PasswordError
    data object NoUpperCase : PasswordError
    data object NoDigit : PasswordError
}