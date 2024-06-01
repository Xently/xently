package co.ke.xently.features.auth.presentation.login

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
) {
    val enableSignInButton: Boolean by derivedStateOf {
        !isLoading
                && email.isNotBlank()
                && password.length >= 6
    }
}