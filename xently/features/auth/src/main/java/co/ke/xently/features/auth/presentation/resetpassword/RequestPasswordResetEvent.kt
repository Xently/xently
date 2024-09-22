package co.ke.xently.features.auth.presentation.resetpassword

import co.ke.xently.libraries.data.core.UiText


sealed interface RequestPasswordResetEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : RequestPasswordResetEvent

    data object Success : RequestPasswordResetEvent
}
