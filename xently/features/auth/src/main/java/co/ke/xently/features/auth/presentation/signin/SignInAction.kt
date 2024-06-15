package co.ke.xently.features.auth.presentation.signin

import android.content.Context
import co.ke.xently.features.auth.data.domain.GoogleUser

internal sealed interface SignInAction {
    data class ChangeEmail(val email: String) : SignInAction
    data class ChangePassword(val password: String) : SignInAction
    data object TogglePasswordVisibility : SignInAction
    data object ClickSubmitCredentials : SignInAction
    class ClickSignInWithGoogle(val activityContext: Context) : SignInAction
    data class FinaliseGoogleSignIn(val user: GoogleUser) : SignInAction
}