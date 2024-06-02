package co.ke.xently.features.auth.presentation.signup

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
) {
    val enableSignUpButton: Boolean by derivedStateOf {
        !isLoading
                && name.isNotBlank()
                && email.isNotBlank()
                && password.length >= 6
    }
}