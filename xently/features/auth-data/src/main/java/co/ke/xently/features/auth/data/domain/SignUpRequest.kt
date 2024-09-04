package co.ke.xently.features.auth.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val firstName: String?,
    val lastName: String?,
    val emailAddress: String,
    val password: String,
)

data class Name(
    val firstName: String?,
    val lastName: String?,
)