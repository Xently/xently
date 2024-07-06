package co.ke.xently.features.auth.presentation.signup

import co.ke.xently.features.auth.presentation.utils.UiText


sealed interface SignUpEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : SignUpEvent

    data object Success : SignUpEvent
}
