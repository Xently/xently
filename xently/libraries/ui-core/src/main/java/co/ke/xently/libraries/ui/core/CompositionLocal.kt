package co.ke.xently.libraries.ui.core

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import co.ke.xently.libraries.data.auth.AuthenticationState
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import io.ktor.client.HttpClient

@Immutable
fun interface AuthenticationEventHandler {
    fun requestAuthentication()
}

internal val NoopAuthenticationEventHandler = AuthenticationEventHandler { }

val LocalAuthenticationEventHandler = staticCompositionLocalOf {
    NoopAuthenticationEventHandler
}
val LocalDispatchersProvider = staticCompositionLocalOf<DispatchersProvider> {
    DispatchersProvider.Default
}
val LocalHttpClient = staticCompositionLocalOf<HttpClient> {
    HttpClient()
}

val LocalAuthenticationState = compositionLocalOf<State<AuthenticationState>> {
    mutableStateOf(AuthenticationState())
}