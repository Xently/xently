package co.ke.xently.features.auth.presentation.signin

import co.ke.xently.features.auth.data.domain.GoogleUser
import co.ke.xently.libraries.data.core.UiText


sealed interface SignInEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : SignInEvent

    data class GetGoogleAccessToken(val user: GoogleUser) : SignInEvent
    data object Success : SignInEvent
}
