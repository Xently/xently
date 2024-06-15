package co.ke.xently.libraries.data.auth

import androidx.compose.runtime.Stable

@Stable
data class CurrentUser(
    val id: String,
    val email: String? = null,
    val emailVerified: Boolean = false,
    val name: String? = null,
    val profilePicUrl: String? = null,
)
