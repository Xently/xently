package co.ke.xently.features.profile.presentation.detail

internal sealed interface ProfileDetailAction {
    data object Refresh : ProfileDetailAction
}