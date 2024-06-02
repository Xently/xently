package co.ke.xently.features.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data object AuthenticationNavGraph{
    @Serializable
    internal data object SignIn
    @Serializable
    internal data object SignUp
    @Serializable
    internal data object RequestPasswordReset
}