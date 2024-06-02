package co.ke.xently.features.auth.presentation.resetpassword

internal sealed interface RequestPasswordResetAction {
    data class ChangeEmail(val email: String) : RequestPasswordResetAction
    data object ClickSubmitCredentials : RequestPasswordResetAction
}