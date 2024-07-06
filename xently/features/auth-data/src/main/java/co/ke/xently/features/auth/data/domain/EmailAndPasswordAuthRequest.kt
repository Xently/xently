package co.ke.xently.features.auth.data.domain

import kotlinx.serialization.Serializable

@Serializable
internal data class EmailAndPasswordAuthRequest(
    val email: String,
    val password: String,
)