package co.ke.xently.features.auth.presentation.signin

internal sealed interface SignInAction {
    data class ChangeEmail(val email: String) : SignInAction
    data class ChangePassword(val password: String) : SignInAction
    data object TogglePasswordVisibility : SignInAction
    data object ClickSubmitCredentials : SignInAction
    data object ClickSignInWithGoogle : SignInAction
}