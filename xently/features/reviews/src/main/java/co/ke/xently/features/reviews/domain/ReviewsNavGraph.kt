package co.ke.xently.features.reviews.domain

import kotlinx.serialization.Serializable

@Serializable
data object ReviewsNavGraph {
    @Serializable
    internal data object Reviews

    @Serializable
    internal data object SignUp

    @Serializable
    internal data object RequestPasswordReset
}