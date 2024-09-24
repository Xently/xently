package co.ke.xently.features.auth.data.domain.error

import co.ke.xently.features.auth.data.R
import co.ke.xently.libraries.data.core.UiText

sealed interface PasswordError : LocalFieldError {
    class TooShort(val minimumLength: Int) : PasswordError
    data object NoUpperCase : PasswordError
    data object NoDigit : PasswordError

    override suspend fun toUiText(): UiText {
        return when (this) {
            NoUpperCase -> UiText.StringResource(R.string.error_password_no_uppercase)
            NoDigit -> UiText.StringResource(R.string.error_password_no_digit)
            is TooShort -> UiText.StringResource(
                R.string.error_password_too_short,
                arrayOf(minimumLength),
            )
        }
    }
}