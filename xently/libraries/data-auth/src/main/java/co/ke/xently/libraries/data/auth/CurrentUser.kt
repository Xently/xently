package co.ke.xently.libraries.data.auth

import androidx.compose.runtime.Stable

@Stable
data class CurrentUser(
    val uid: Int,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
) {
    val displayName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
}
