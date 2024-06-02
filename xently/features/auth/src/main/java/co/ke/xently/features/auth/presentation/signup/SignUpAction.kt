package co.ke.xently.features.auth.presentation.signup

internal sealed interface SignUpAction {
    data class ChangeName(val name: String) : SignUpAction
    data class ChangeEmail(val email: String) : SignUpAction
    data class ChangePassword(val password: String) : SignUpAction
    data object TogglePasswordVisibility : SignUpAction
    data object ClickSubmitCredentials : SignUpAction
}