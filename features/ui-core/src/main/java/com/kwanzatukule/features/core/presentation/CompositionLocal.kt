package com.kwanzatukule.features.core.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.kwanzatukule.features.core.domain.models.AuthenticationState


val LocalAuthenticationState = staticCompositionLocalOf {
    AuthenticationState()
}