package co.ke.xently.features.auth.presentation.login

import co.ke.xently.features.auth.presentation.utils.UiText


sealed interface SignInEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : SignInEvent
}
