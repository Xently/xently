package com.kwanzatukule.features.core.domain.models

import androidx.compose.runtime.Stable

@Stable
data class AuthenticationState(
    val isSignOutInProgress: Boolean = false,
    val currentUser: CurrentUser? = null,
)
