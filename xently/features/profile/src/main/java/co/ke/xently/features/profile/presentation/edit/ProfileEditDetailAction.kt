package co.ke.xently.features.profile.presentation.edit

internal sealed interface ProfileEditDetailAction {
    data object ClickSave : ProfileEditDetailAction
    class ChangeName(val name: String) : ProfileEditDetailAction
}