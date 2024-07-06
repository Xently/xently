package co.ke.xently.libraries.data.auth

import androidx.compose.runtime.Stable

@Stable
data class AuthenticationState(
    val isSignOutInProgress: Boolean = false,
    val currentUser: CurrentUser? = null,
) {
    val isAuthenticated: Boolean
        get() = currentUser != null
}
