package co.ke.xently.features.auth.presentation.resetpassword

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import co.ke.xently.features.auth.data.domain.error.FieldError

@Stable
data class RequestPasswordResetUiState(
    val email: String = "",
    val emailError: List<FieldError>? = null,
    val isLoading: Boolean = false,
) {
    val enableRequestPasswordResetButton: Boolean by derivedStateOf {
        !isLoading
                && email.isNotBlank()
    }
    val isFormValid: Boolean = emailError.isNullOrEmpty()
}