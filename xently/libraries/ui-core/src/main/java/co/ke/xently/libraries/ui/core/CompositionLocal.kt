package co.ke.xently.libraries.ui.core

import androidx.compose.runtime.staticCompositionLocalOf
import co.ke.xently.libraries.data.auth.AuthenticationState


val LocalAuthenticationState = staticCompositionLocalOf {
    AuthenticationState()
}