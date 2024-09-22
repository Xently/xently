package co.ke.xently.features.profile.presentation.detail

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ProfileDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.profile.data.domain.error.Error,
    ) : ProfileDetailEvent

    data class Success(val action: ProfileDetailAction) : ProfileDetailEvent
}
