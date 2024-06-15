package co.ke.xently.features.auth.data.domain

import kotlinx.serialization.Serializable

@Serializable
internal data class GoogleAuthRequest(
    val idToken: String,
    val accessToken: String? = null,
    val displayName: String = "",
    val profilePicUrl: String? = null,
)