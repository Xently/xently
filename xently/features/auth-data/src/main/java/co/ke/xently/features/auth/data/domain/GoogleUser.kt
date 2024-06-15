package co.ke.xently.features.auth.data.domain

data class GoogleUser(
    val id: String,
    val idToken: String,
    val accessToken: String? = null,
    val displayName: String = "",
    val profilePicUrl: String? = null,
)