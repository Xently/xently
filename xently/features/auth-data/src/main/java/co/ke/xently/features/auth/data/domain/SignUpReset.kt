package co.ke.xently.features.auth.data.domain

import kotlinx.serialization.Serializable

@Serializable
internal data class SignUpReset(
    val firstName: String?,
    val lastName: String?,
    val emailAddress: String,
    val password: String,
)