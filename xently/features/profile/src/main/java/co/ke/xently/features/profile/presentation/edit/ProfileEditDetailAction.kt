package co.ke.xently.features.profile.presentation.edit

internal sealed interface ProfileEditDetailAction {
    data object ClickSave : ProfileEditDetailAction
    class ChangeFirstName(val name: String) : ProfileEditDetailAction
    class ChangeLastName(val name: String) : ProfileEditDetailAction
    class ChangeEmail(val email: String) : ProfileEditDetailAction
}