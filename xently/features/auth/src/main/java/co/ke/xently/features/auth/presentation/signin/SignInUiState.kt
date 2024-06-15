package co.ke.xently.features.auth.presentation.signin

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import co.ke.xently.features.auth.data.domain.GoogleUser

@Stable
data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val currentGoogleUser: GoogleUser? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
) {
    val enableSignInButton: Boolean by derivedStateOf {
        !isLoading
                && email.isNotBlank()
                && password.length >= 6
    }
}