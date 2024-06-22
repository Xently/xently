package co.ke.xently.libraries.ui.core

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import co.ke.xently.libraries.data.auth.AuthenticationState

@Immutable
fun interface AuthenticationEventHandler {
    fun requestAuthentication()
}

internal val NoopAuthenticationEventHandler = AuthenticationEventHandler { }

val LocalAuthenticationEventHandler = staticCompositionLocalOf {
    NoopAuthenticationEventHandler
}

val LocalAuthenticationState = staticCompositionLocalOf {
    AuthenticationState()
}