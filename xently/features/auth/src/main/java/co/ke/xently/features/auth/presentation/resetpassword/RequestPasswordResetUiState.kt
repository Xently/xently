package co.ke.xently.features.auth.presentation.resetpassword

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
data class RequestPasswordResetUiState(
    val email: String = "",
    val isLoading: Boolean = false,
) {
    val enableRequestPasswordResetButton: Boolean by derivedStateOf {
        !isLoading
                && email.isNotBlank()
    }
}