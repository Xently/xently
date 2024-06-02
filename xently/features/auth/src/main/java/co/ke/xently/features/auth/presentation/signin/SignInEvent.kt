package co.ke.xently.features.auth.presentation.signin

import co.ke.xently.features.auth.presentation.utils.UiText


sealed interface SignInEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : SignInEvent

    data object Success : SignInEvent
}
