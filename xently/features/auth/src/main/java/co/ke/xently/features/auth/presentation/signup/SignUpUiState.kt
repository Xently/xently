package co.ke.xently.features.auth.presentation.signup

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import co.ke.xently.features.auth.data.domain.error.FieldError

@Stable
data class SignUpUiState(
    val name: String = "",
    val nameError: List<FieldError>? = null,
    val email: String = "",
    val emailError: List<FieldError>? = null,
    val password: String = "",
    val passwordError: List<FieldError>? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
) {
    val enableSignUpButton: Boolean by derivedStateOf {
        !isLoading
    }
    val isFormValid: Boolean = nameError.isNullOrEmpty()
            && passwordError.isNullOrEmpty()
            && emailError.isNullOrEmpty()
}