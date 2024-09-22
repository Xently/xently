package co.ke.xently.features.profile.presentation.edit

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ProfileEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.profile.data.domain.error.Error,
    ) : ProfileEditDetailEvent

    data class Success(val action: ProfileEditDetailAction) : ProfileEditDetailEvent
}
